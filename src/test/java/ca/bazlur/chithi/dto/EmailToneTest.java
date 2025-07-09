package ca.bazlur.chithi.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailToneTest {

    @Test
    void testEnumValues() {
        // Verify all expected values exist
        assertThat(EmailTone.values()).containsExactly(
                EmailTone.PROFESSIONAL,
                EmailTone.CASUAL,
                EmailTone.BUSINESS_CASUAL,
                EmailTone.FRIENDLY,
                EmailTone.FORMAL,
                EmailTone.CONVERSATIONAL,
                EmailTone.EMPATHETIC,
                EmailTone.DIRECT,
                EmailTone.ENTHUSIASTIC
        );
    }

    @Test
    void testDisplayNames() {
        // Verify display names
        assertThat(EmailTone.PROFESSIONAL.getDisplayName()).isEqualTo("professional");
        assertThat(EmailTone.CASUAL.getDisplayName()).isEqualTo("casual");
        assertThat(EmailTone.BUSINESS_CASUAL.getDisplayName()).isEqualTo("business casual");
        assertThat(EmailTone.FRIENDLY.getDisplayName()).isEqualTo("friendly");
        assertThat(EmailTone.FORMAL.getDisplayName()).isEqualTo("formal");
        assertThat(EmailTone.CONVERSATIONAL.getDisplayName()).isEqualTo("conversational");
        assertThat(EmailTone.EMPATHETIC.getDisplayName()).isEqualTo("empathetic");
        assertThat(EmailTone.DIRECT.getDisplayName()).isEqualTo("direct");
        assertThat(EmailTone.ENTHUSIASTIC.getDisplayName()).isEqualTo("enthusiastic");
    }

    @Test
    void testToString() {
        // Verify toString returns display name
        for (EmailTone tone : EmailTone.values()) {
            assertThat(tone.toString()).isEqualTo(tone.getDisplayName());
        }
    }

    @Test
    void testValueOf() {
        // Test valueOf for all enum constants
        assertThat(EmailTone.valueOf("PROFESSIONAL")).isEqualTo(EmailTone.PROFESSIONAL);
        assertThat(EmailTone.valueOf("BUSINESS_CASUAL")).isEqualTo(EmailTone.BUSINESS_CASUAL);
    }

    @Test
    void testEnumOrdinal() {
        // Verify ordinal values
        assertThat(EmailTone.PROFESSIONAL.ordinal()).isEqualTo(0);
        assertThat(EmailTone.CASUAL.ordinal()).isEqualTo(1);
        assertThat(EmailTone.BUSINESS_CASUAL.ordinal()).isEqualTo(2);
    }
}