package com.x930073498.recycler;

public class Options {
    private boolean hasConsistItemType;

    public boolean isHasConsistItemType() {
        return hasConsistItemType;
    }


    private Options(Builder builder) {
        hasConsistItemType = builder.hasConsistItemType;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder {
        private boolean hasConsistItemType;

        private Builder() {
        }

        public Builder hasConsistItemType(boolean hasConsistItemType) {
            this.hasConsistItemType = hasConsistItemType;
            return this;
        }


        public Options create() {
            return new Options(this);
        }
    }
}
