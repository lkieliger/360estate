package ch.epfl.sweng.project;


import ch.epfl.sweng.project.list.Item;

public class Filter {

    int price;
    String location;
    Item.HouseType type;
    int nbrOfRooms;
    int surface;

    private Boolean isPriceFiltered;
    private Boolean isLocationFiltered;
    private Boolean isTypeFiltered;
    private Boolean isNbrOfRoomsFiltered;
    private Boolean isSurfaceFiltered;

    public Filter(int price, String location, Item.HouseType type, int nbrOfRooms, int surface) {
        this.price = price;
        this.location = location;
        this.type = type;
        this.nbrOfRooms = nbrOfRooms;
        this.surface = surface;
    }
}
