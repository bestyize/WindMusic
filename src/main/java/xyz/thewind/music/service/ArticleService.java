package xyz.thewind.music.service;

import xyz.thewind.music.dto.ArticleDTO;
import xyz.thewind.music.model.Article;

import java.util.List;

public interface ArticleService {
    Article addArticle(ArticleDTO articleDTO);
    Article findArticleByArticleId(String articleId);
    List<Article> findArticleByUserId(String userId);
}
