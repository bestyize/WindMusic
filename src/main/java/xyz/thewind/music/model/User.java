package xyz.thewind.music.model;

import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "tb_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id",columnDefinition = "varchar(32)",nullable = false,unique = true)
    private String userId;
    @Column(name = "password",columnDefinition = "varchar(32)",nullable = false)
    private String password;
    @Column(name = "user_name",columnDefinition = "varchar(32)",nullable = false)
    private String userName;
    @Column(name = "email",columnDefinition = "varchar(32)",nullable = true,unique = true)
    private String email;
    @Column(name = "mobile",columnDefinition = "varchar(11)",nullable = true,unique = true)
    private String mobile;
    @Column(name = "birthday",columnDefinition = "timestamp",nullable = false)
    private Date birthday;
    @Column(name = "create_date",columnDefinition = "timestamp",nullable = false)
    private Date createDate;
    @Column(name = "level",columnDefinition = "int",nullable = false)
    private Integer level;
    @Column(name = "status",columnDefinition = "int",nullable = false)
    private Integer status;
    @Column(name = "avatar",columnDefinition = "varchar(200)",nullable = true)
    private String avatar;

    public User() {
    }

    public User(String userId, String password, String userName, String email, String mobile, Date birthday, Date createDate, Integer level, Integer status, String avatar) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.mobile = mobile;
        this.birthday = birthday;
        this.createDate = createDate;
        this.level = level;
        this.status = status;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", birthday=" + birthday +
                ", createDate=" + createDate +
                ", level=" + level +
                ", status=" + status +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
