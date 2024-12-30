package io.github.sever0x.testentitybuilder.core.generator;

import com.google.auto.service.AutoService;
import io.github.sever0x.testentitybuilder.annotation.GenerateBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Annotation processor for generating builder classes for annotated entity classes.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("io.github.sever0x.testentitybuilder.annotation.GenerateBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BuilderGenerator extends AbstractProcessor {

    /**
     * Initializes the processor with the environment.
     *
     * @param processingEnv The processing environment.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "BuilderGenerator initialization started");
    }

    /**
     * Processes the annotations and generates builder classes for eligible entity classes.
     *
     * @param annotations The set of annotations to process.
     * @param roundEnv    The environment of the current annotation processing round.
     * @return {@code true} if the annotations were processed successfully, otherwise {@code false}.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "BuilderGenerator processing started with annotations: " + annotations);

        TypeElement annotationElement = processingEnv.getElementUtils().getTypeElement(GenerateBuilder.class.getCanonicalName());

        if (annotationElement == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not find GenerateBuilder annotation class");
            return false;
        }

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(GenerateBuilder.class);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Found " + annotatedElements.size() + " elements with @GenerateBuilder annotation");

        for (Element element : annotatedElements) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@GenerateBuilder can only be applied to classes, but was applied to " + element.getKind(), element);
                continue;
            }

            if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@GenerateBuilder cannot be applied to abstract classes", element);
                continue;
            }

            try {
                TypeElement typeElement = (TypeElement) element;
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating builder for " + typeElement.getQualifiedName());
                generateBuilder(typeElement);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Successfully generated builder for " + typeElement.getQualifiedName());
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate builder for " + element + ": " + e.getMessage(), element);
            }
        }

        return true;
    }

    private void generateBuilder(TypeElement typeElement) {
        String className = typeElement.getSimpleName() + "Builder";
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        String fullClassName = packageName + "." + className;

        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(fullClassName);

            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                writePackageAndImports(out, packageName, typeElement);

                out.println("public class " + className + " extends AbstractEntityBuilder<" +
                       getFullClassName(typeElement) + ", " + className + "> {");

                out.println("    public " + className + "() {");
                out.println("        super(" + getFullClassName(typeElement) + ".class);");
                out.println("    }");
                out.println();

                List<VariableElement> allFields = getAllFields(typeElement);

                for (VariableElement field : allFields) {
                    generateWithMethod(out, field, className);
                }

                out.println("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printError("Failed to generate builder for " + typeElement.getSimpleName() + ": " + e.getMessage());
        }
    }

    private void generateWithMethod(PrintWriter out, VariableElement field, String builderClassName) {
        String fieldName = field.getSimpleName().toString();
        TypeMirror fieldType = field.asType();
        String capitalizedFieldName = capitalize(fieldName);

        out.println("    public " + builderClassName + " with" + capitalizedFieldName + "(" + fieldType + " value) {");
        out.println("        delegate.with(\"" + fieldName + "\", value);");
        out.println("        return self();");
        out.println("    }");
        out.println();
    }

    private void writePackageAndImports(PrintWriter out, String packageName, TypeElement typeElement) {
        out.println("package " + packageName + ";");
        out.println();
        out.println("import io.github.sever0x.testentitybuilder.core.builder.AbstractEntityBuilder;");

        TypeElement currentClass = typeElement;
        while (currentClass != null && !currentClass.getQualifiedName().toString().equals("java.lang.Object")) {
            String currentPackage = processingEnv.getElementUtils().getPackageOf(currentClass).toString();
            if (!currentPackage.equals(packageName)) {
                out.println("import " + currentClass.getQualifiedName() + ";");
            }

            TypeMirror superclass = currentClass.getSuperclass();
            if (superclass == null) {
                break;
            }
            currentClass = (TypeElement) processingEnv.getTypeUtils().asElement(superclass);
        }
        out.println();
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getFullClassName(TypeElement typeElement) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Resolving full class name for: " + typeElement.getSimpleName());
        StringBuilder fullName = new StringBuilder();
        Element enclosing = typeElement.getEnclosingElement();
        if (enclosing != null && enclosing.getKind() == ElementKind.CLASS) {
            String enclosingClassName = ((TypeElement) enclosing).getQualifiedName().toString();
            fullName.append(enclosingClassName).append(".");
        }
        fullName.append(typeElement.getSimpleName());
        return fullName.toString();
    }

    private List<VariableElement> getAllFields(TypeElement typeElement) {
        List<VariableElement> fields = new ArrayList<>();
        TypeElement currentClass = typeElement;

        while (currentClass != null && !currentClass.getQualifiedName().toString().equals("java.lang.Object")) {
            for (Element element : currentClass.getEnclosedElements()) {
                if (element.getKind() == ElementKind.FIELD) {
                    VariableElement field = (VariableElement) element;
                    if (!isFieldEligible(field)) {
                        continue;
                    }
                    fields.add(field);
                }
            }

            TypeMirror superclass = currentClass.getSuperclass();
            if (superclass == null) {
                break;
            }
            currentClass = (TypeElement) processingEnv.getTypeUtils().asElement(superclass);
        }
        return fields;
    }

    private boolean isFieldEligible(VariableElement field) {
        Set<Modifier> modifiers = field.getModifiers();
        return !modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.FINAL);
    }


}
