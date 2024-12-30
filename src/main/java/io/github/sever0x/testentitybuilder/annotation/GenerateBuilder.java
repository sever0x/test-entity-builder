package io.github.sever0x.testentitybuilder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that marks a class for automatic builder generation.
 * When applied to a class, a corresponding builder class will be generated during compilation.
 *
 * <p>The generated builder class will follow the builder pattern and provide:
 * <ul>
 *     <li>Type-safe methods for setting each field</li>
 *     <li>Method chaining capability</li>
 *     <li>Automatic handling of primitive and object types</li>
 *     <li>Support for inheritance hierarchies</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @GenerateBuilder
 * public class Person {
 *     private String name;
 *     private int age;
 *
 *     // getters and setters
 * }
 *
 * // Usage of generated builder:
 * Person person = new PersonBuilder()
 *     .withName("John")
 *     .withAge(25)
 *     .build();
 * }
 * </pre>
 *
 * @see io.github.sever0x.testentitybuilder.core.EntityBuilder
 * @see io.github.sever0x.testentitybuilder.core.builder.AbstractEntityBuilder
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateBuilder {
}
