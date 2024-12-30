package io.github.sever0x.testentitybuilder.inheritance;

import io.github.sever0x.testentitybuilder.annotation.GenerateBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Generated Builder Inheritance Tests")
class GeneratedBuilderInheritanceTest {

    @GenerateBuilder
    public static class BaseEntity {
        private Long id;
        private String createdBy;
        private LocalDateTime createdAt;

        public BaseEntity() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    @GenerateBuilder
    public static class Product extends BaseEntity {
        private String name;
        private BigDecimal price;

        public Product() {
            super();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    @Nested
    @DisplayName("Generated Builder Tests")
    class GeneratedBuilderTests {

        @Test
        @DisplayName("Should build Product using generated builder with inherited fields")
        void shouldBuildProductWithInheritedFields() {
            // When
            Product product = new ProductBuilder()
                    .withId(100L)
                    .withCreatedBy("admin")
                    .withCreatedAt(LocalDateTime.now())
                    .withName("Test Product")
                    .withPrice(new BigDecimal("99.99"))
                    .build();

            // Then
            assertThat(product.getId()).isEqualTo(100L);
            assertThat(product.getCreatedBy()).isEqualTo("admin");
            assertThat(product.getCreatedAt()).isNotNull();
            assertThat(product.getName()).isEqualTo("Test Product");
            assertThat(product.getPrice()).isEqualTo(new BigDecimal("99.99"));
        }

        @Test
        @DisplayName("Should set default values for all fields including inherited ones")
        void shouldSetDefaultValuesForAllFields() {
            // When
            Product product = new ProductBuilder().build();

            // Then
            assertThat(product.getId()).isNotNull();
            assertThat(product.getCreatedBy()).isNotNull();
            assertThat(product.getCreatedAt()).isNotNull();
            assertThat(product.getName()).isNotNull();
            assertThat(product.getPrice()).isNotNull();
        }
    }
}