package restaurantMenu;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class App {
    private static EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("jpaRestaurant");
    private static EntityManager manager = managerFactory.createEntityManager();
    private static Session session=manager.unwrap(Session.class);
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            manager.getTransaction().begin();
            try {
                fillMenu();
            } catch (Exception ex) {
                System.out.println(" rollback 1");
                manager.getTransaction().rollback();
            }
            while (true) {
 System.out.println("1: Choice of food  - the criterion is Cost \"From\" - \"To\"  (first option -choiceDishesCostFromTo)");
 System.out.println("2: Choice of food  - the criterion is Cost \"From\" - \"To\" (second option -criteria API  -searchMinMax"); // criteria - session

 System.out.println("3: Choice of food  - the criterion is Dishes with discount only (first option -discountDishes)");
 System.out.println("4: Choice of food  - the criterion is Dishes with discount only (second option -findAllDishesWithDiscount)"); // with criteria API

 System.out.println("5: Choice a set of dishes of no more than a certain weight (allDishesButNoMoreWeight)");
 /* позаимствовал у парня метод - интересный , но не рабочий  - поиск  по весу до 1(можно поменять) кг (так... для памяти) */
 System.out.println("6: searchWeight");

 System.out.println("7: Add a Dish to the Menu");

                System.out.print("-> ");
                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        choiceDishesCostFromTo(sc);
                        break;
                    case "2":
                        searchMinMax(sc, session);
                        break;


                    case "3":
                        discountDishes(sc);
                        break;
                    case "4":
                        findAllDishesWithDiscount();
                        break;


                    case "5":
                        allDishesButNoMoreWeight(sc);
                        break;
                    case "6":
                        searchWeight();
                        break;

                    case "7":
                        addDish(sc);
                        break;

                    default:
                        return;
                }
            }
        } finally {
            manager.close();
            managerFactory.close();
        }
    }
    private static void choiceDishesCostFromTo(Scanner sc) {
        System.out.println("Enter \"from\" price:");
        String low=sc.nextLine();
        double lowprice = Double.parseDouble(low);
        System.out.println("Enter \"to\" price:");
        String high=sc.nextLine();
        double highprice = Double.parseDouble(high);
        List<Dish> d;
        try{
            Query query= manager.createQuery("select d from Dish d where d.price>= :low and  d.price<= :high", Dish .class);
            query.setParameter("low", lowprice);
            query.setParameter("high", highprice);
            d= (List<Dish>) query.getResultList();
            for (Dish dish: d  ) {
                System.out.println(dish);
            }
        } catch (NoResultException ex) {
            System.out.println("Client not found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }
    }
    private static void searchMinMax(Scanner sc, Session session) {
        System.out.println("Input min price");
        Double minPrice = Double.parseDouble(sc.nextLine());
        System.out.println("Input max price");
        Double maxPrice = Double.parseDouble(sc.nextLine());

        List<Dish> result = session.createCriteria(Dish.class)
                .add(Restrictions.between("price",minPrice,maxPrice))
                .list();
        for (Dish item:  result) {
            System.out.println(item);
        }
    }
    private static void discountDishes(Scanner sc) {
        System.out.println("Enter discount:");
        String discount=sc.nextLine();
        int minDiscount = Integer.parseInt(discount);
        List<Dish> d;
        try{
            Query query= manager.createQuery("select d from Dish d where d.discount> :sumDiscount", Dish .class);
            query.setParameter("sumDiscount", minDiscount);
            d= (List<Dish>) query.getResultList();
            for (Dish dish: d  ) {
                System.out.println(dish);
            }
        } catch (NoResultException ex) {
            System.out.println("Client not found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }
    }
    private static void findAllDishesWithDiscount() {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        Metamodel model = manager.getMetamodel();
        EntityType<Dish> dishType = model.entity(Dish.class);
        CriteriaQuery<Dish> criteriaQuery = criteriaBuilder.createQuery(Dish.class);
        Root<Dish> dish = criteriaQuery.from(dishType);
        Expression<Double> field = dish.get("discount");
        criteriaQuery.select(dish).where(criteriaBuilder.gt(field,0),criteriaBuilder.isNotNull(field));
        TypedQuery<Dish> q = manager.createQuery(criteriaQuery);
        List<Dish> dishes=q.getResultList();
        for (Dish dish1:dishes ) {
            System.out.println(dish1);
        }
    }
    private static void allDishesButNoMoreWeight(Scanner sc) {
        System.out.println("Select dish, please, from  list: ");
        Query query= manager.createQuery("select d from  Dish d", Dish.class);
        List<Dish> d= query.getResultList();

        for (Dish dish: d  ) {
            System.out.println(dish.getId()+" "+dish.getName() + " - price "+ dish.getPrice()+" - weight"+ dish.getWeight());
        }
        boolean b=true;
        double total_weight=0;
        List<Dish> order= new ArrayList<>();
        while (b){
            System.out.println("Enter id :");
            String dish=sc.nextLine();
            long id = Long.parseLong(dish);
            try {
                query = manager.createQuery("select d from  Dish d where d.id= :id", Dish.class);
                query.setParameter("id", id);
                Dish dish1 = (Dish) query.getSingleResult();
                total_weight=total_weight+dish1.getWeight();
                if(total_weight<1) {order.add(dish1);}
                else {
                    System.out.println("weight >1"+ " - Total weight :"+ total_weight);
                    System.out.println();
                    System.out.println("Your order:");
                    for (Dish dish2: order) {
                        System.out.println(dish2.getId()+" "+dish2.getName() + " - price "+ dish2.getPrice()+" - weight"+ dish2.getWeight());
                    }
                    b=false;
                }
            } catch (NoResultException ex) {
                System.out.println("Client not found!");
            } catch (NonUniqueResultException ex) {
                System.out.println("Non unique result!");
            }

            /*System.out.println("if that's enough? - 1");
            String enough=sc.nextLine();
            if(enough.equals("1")){b=false; }*/
        }
    }
    private static void searchWeight( ) {
        double leftWeight = 1;
        List<Dish> result = new ArrayList<>();
        Query query = manager.createQuery("SELECT count(id) AS id FROM Dish");
        Long count =(Long) query.getSingleResult();
        long i = 0;
        while (true){
/*(если удалить блюдо, потом добавить другое,id удалённого всё равно может быть выбрано
(long randId = (long)(Math.random()*count+1);) Dish temp = manager.find(Dish.class,randId); - NULL - так как блюдо
удалено и вылетит - NullPointerException) в  - temp.getWeight()*/
            long randId = (long)(Math.random()*count+1);
            Dish temp = manager.find(Dish.class,randId);
            if (leftWeight >= temp.getWeight()){
                result.add(temp);
                leftWeight=leftWeight-temp.getWeight();
            }
            i++;
            if (i==(count*10)) break;
        }
        for (Dish item : result) {
            System.out.println(item);
        }
    }
    private static void addDish(Scanner sc) {
        System.out.println("Enter Dish name ");
        String name= sc.nextLine();
        System.out.println("Enter Dish price ");
        String pr=sc.nextLine();
        double price = Double.parseDouble(pr);
        System.out.println("Enter Dish weight ");
        String wgh=sc.nextLine();
        double weight = Double.parseDouble(wgh);
        System.out.println("Enter Dish discount ");
        String disc=sc.nextLine();
        int discount = Integer.parseInt(disc);

        String s = "Celebration";
        Query query=manager.createQuery("select m from MenuRestaurant m where m.name = :name", MenuRestaurant.class);
        query.setParameter("name", s);
        MenuRestaurant menuRestaurant=(MenuRestaurant)query.getSingleResult();

        manager.getTransaction().begin();
        try {
            Dish dish = new Dish(name, price, weight, discount);
            menuRestaurant.addDishes(dish);
            manager.persist(menuRestaurant);
            manager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(" rollback 2");
            manager.getTransaction().rollback();
        }

    }
    private static void fillMenu() {
        MenuRestaurant menuRestaurant = new MenuRestaurant("Celebration");
        Dish dish = new Dish("Olivie", 12, 0.150, 5);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("cheeses", 26, 0.050);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("salad", 10, 0.150, 5);
        menuRestaurant.addDishes(dish);
        dish = new Dish("cucumber", 3, 0.100, 15);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("bread", 2, 0.250);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("meat", 30, 0.300, 10);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("hot Dog", 23, 0.200, 35);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("pizza", 34, 0.500, 40);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("egg", 15, 0.180);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("avocado", 45, 0.150);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        dish = new Dish("fruit", 10, 0.50);
        menuRestaurant.addDishes(dish);
        manager.persist(menuRestaurant);
        manager.getTransaction().commit();
    }
}
