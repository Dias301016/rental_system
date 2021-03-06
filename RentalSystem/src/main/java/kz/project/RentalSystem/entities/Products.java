package kz.project.RentalSystem.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_products")
public class Products implements Comparable<Products>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private int price;

    @ManyToOne(fetch = FetchType.EAGER)
    private Categories category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users author;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Keywords> keywords;

    @Column(name = "post_date")
    private Date postDate;

    @Column(name = "product_picture")
    private String productPicture;

    @Override
    public int compareTo(Products product) {
        return product.getPostDate().compareTo(this.getPostDate());
    }


}
