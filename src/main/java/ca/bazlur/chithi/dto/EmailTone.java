package ca.bazlur.chithi.dto;

public enum EmailTone {
  PROFESSIONAL("professional"),
  CASUAL("casual"),
  BUSINESS_CASUAL("business casual"),
  FRIENDLY("friendly"),
  FORMAL("formal"),
  CONVERSATIONAL("conversational"),
  EMPATHETIC("empathetic"),
  DIRECT("direct"),
  ENTHUSIASTIC("enthusiastic");

  private final String displayName;

  EmailTone(String displayName) {
    this.displayName = displayName;
  }
  
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}