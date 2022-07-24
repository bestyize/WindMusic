package xyz.thewind.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.thewind.music.model.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    Article findTopByArticleId(String articleId);

    List<Article> findArticlesByUserId(String userId);
}
