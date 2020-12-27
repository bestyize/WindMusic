package xyz.thewind.music.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * public class Article {
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *     @Column(name = "article_title",columnDefinition = "varchar(64)",nullable = false,unique = true)
 *     private String articleTitle;
 *     @Column(name = "content",columnDefinition = "text",nullable = false)
 *     private String content;
 *     @Column(name = "article_url",columnDefinition = "varchar(64)",nullable = false,unique = true)
 *     private String articleUrl;
 *     @Column(name = "type",columnDefinition = "int",nullable = false)
 *     private Integer type;
 *     @Column(name = "publish_time",columnDefinition = "timestamp",nullable = false)
 *     private Date publishTime;
 *     @Column(name = "cover_url",columnDefinition = "varchar(64)")
 *     private String coverUrl;
 *     @Column(name = "user_id",columnDefinition = "varchar(64)",nullable = false)
 *     private String userId;
 *     @Column(name = "like_count",columnDefinition = "int")
 *     private Integer likeCount;
 *
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDTO {
    private String userId;
    private String articleId;
    private String articleTitle;
    private String articleContent;
    private String coverUrl;

}
