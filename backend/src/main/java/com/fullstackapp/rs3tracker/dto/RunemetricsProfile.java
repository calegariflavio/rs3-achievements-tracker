package com.fullstackapp.rs3tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RunemetricsProfile {
    @JsonProperty("name")         public String name;
    @JsonProperty("combatlevel")  public int combatLevel;
    @JsonProperty("totalxp")      public long totalXp;
    @JsonProperty("totalskill")   public int totalSkillLevel;
    @JsonProperty("questscomplete")   public int questsComplete;
    @JsonProperty("questsstarted")    public int questsStarted;
    @JsonProperty("questsnotstarted") public int questsNotStarted;
    @JsonProperty("rank")         public String rank;
    @JsonProperty("loggedIn")     public String loggedIn;
    @JsonProperty("skillvalues")  public List<SkillValue> skillValues;
    @JsonProperty("activities")   public List<ActivityData> activities;
}
