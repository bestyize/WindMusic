package xyz.thewind.music.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "tb_article")
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "article_id",columnDefinition = "varchar(64)",nullable = false,unique = true)
    private String articleId;
    @Column(name = "article_title",columnDefinition = "varchar(64)",nullable = false,unique = true)
    private String articleTitle;
    @Column(name = "content",columnDefinition = "text",nullable = false)
    private String content;
    @Column(name = "article_url",columnDefinition = "varchar(64)",nullable = false,unique = true)
    private String articleUrl;
    @Column(name = "type",columnDefinition = "int",nullable = false)
    private Integer type;
    @Column(name = "publish_time",columnDefinition = "timestamp",nullable = false)
    private Date publishTime;
    @Column(name = "cover_url",columnDefinition = "varchar(64)")
    private String coverUrl;
    @Column(name = "user_id",columnDefinition = "varchar(64)",nullable = false)
    private String userId;
    @Column(name = "like_count",columnDefinition = "int")
    private Integer likeCount;

    public Article() {
    }

    public Article(String articleId, String articleTitle, String content, String articleUrl, Integer type, Date publishTime, String coverUrl, String userId, Integer likeCount) {
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.content = content;
        this.articleUrl = articleUrl;
        this.type = type;
        this.publishTime = publishTime;
        this.coverUrl = coverUrl;
        this.userId = userId;
        this.likeCount = likeCount;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
}
