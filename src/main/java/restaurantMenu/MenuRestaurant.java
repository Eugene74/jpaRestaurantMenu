package restaurantMenu;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="menurestaurant")
public class MenuRestaurant {
    @Id
    @GeneratedValue
    private long id;
    @Column(name="name", nullable = false)
    private String name;
    @OneToMany(mappedBy = "menuRestaurant", cascade = CascadeType.ALL)
    private List<Dish> dishes = new ArrayList<>();

    public   MenuRestaurant() {
    }

      MenuRestaurant(String name) {
        this.name = name;
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

      List<Dish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

      void addDishes( Dish dish) {
        dish.setMenuRestaurant(this);
        dishes.add(dish);
    }
    @Override
    public String toString() {
        return "MenuRestaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
