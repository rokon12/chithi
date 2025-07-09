package ca.bazlur.chithi.dto;

public enum EmailPurpose {
    AUTO_DETECT("auto-detect"),
    APOLOGY("apology"),
    THANK_YOU("thank you"),
    FOLLOW_UP("follow-up"),
    REQUEST("request"),
    INTRODUCTION("introduction"),
    INVITATION("invitation"),
    ANNOUNCEMENT("announcement"),
    FEEDBACK("feedback"),
    COMPLAINT("complaint"),
    INQUIRY("inquiry"),
    CONFIRMATION("confirmation"),
    REJECTION("rejection"),
    ACCEPTANCE("acceptance"),
    RESIGNATION("resignation"),
    RECOMMENDATION("recommendation"),
    CONGRATULATIONS("congratulations"),
    CONDOLENCE("condolence"),
    REMINDER("reminder"),
    PROPOSAL("proposal"),
    REPORT("report"),
    UPDATE("update"),
    GENERAL("general");
    
    private final String displayName;
    
    EmailPurpose(String displayName) {
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