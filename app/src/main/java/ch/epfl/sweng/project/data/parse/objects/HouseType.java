package ch.epfl.sweng.project.data.parse.objects;

import ch.epfl.sweng.project.R;

public enum HouseType {
    APARTMENT(R.string.apartment), HOUSE(R.string.house),
    BUILDING(R.string.building);

    private final int description;

    HouseType(int d) {
        description = d;
    }

    public int getDescription() {
        return description;
    }
}