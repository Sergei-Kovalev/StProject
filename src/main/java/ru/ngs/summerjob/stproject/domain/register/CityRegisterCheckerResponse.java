package ru.ngs.summerjob.stproject.domain.register;

public class CityRegisterCheckerResponse {
    private boolean existing;
    private Boolean temporal = null;

    public boolean isExisting() {
        return existing;
    }

    @Override
    public String toString() {
        return "CityRegisterCheckerResponse{" +
                "existing=" + existing +
                ", temporal=" + temporal +
                '}';
    }

    public void setExisting(boolean existing) {
        this.existing = existing;
    }

    public Boolean getTemporal() {
        return temporal;
    }

    public void setTemporal(Boolean temporal) {
        this.temporal = temporal;
    }
}
