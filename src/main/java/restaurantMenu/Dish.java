package restaurantMenu;

import javax.persistence.*;

@Entity
@Table(name="dish")
public class Dish {
    @Id
    @GeneratedValue()
    private long id;

    @Column(name="name", nullable = false, unique = true )
    private String name;

    @Column
    private double price;

    @Column
    private double weight;

    @Column
    private int discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private MenuRestaurant menuRestaurant;

    public Dish() {
    }

    Dish(String name, double price, double weight, int discount) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.discount = discount;
    }

     Dish(String name, double price, double weight) {
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

      long getId() {
        return id;
    }

      void setId(long id) {
        this.id = id;
    }

      String getName() {
        return name;
    }

      void setName(String name) {
        this.name = name;
    }

      double getPrice() {
        return price;
    }

      void setPrice(double price) {
        this.price = price;
    }

      double getWeight() {
        return weight;
    }

      void setWeight(double weight) {
        this.weight = weight;
    }

      int getDiscount() {
        return discount;
    }

      void setDiscount(int discount) {
        this.discount = discount;
    }

      MenuRestaurant getMenuRestaurant() {
        return menuRestaurant;
    }

      void setMenuRestaurant(MenuRestaurant menuRestaurant) {
        this.menuRestaurant = menuRestaurant;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                ", discount=" + discount +
                '}';
    }
}
