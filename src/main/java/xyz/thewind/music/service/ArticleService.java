package xyz.thewind.music.service;

import xyz.thewind.music.dto.ArticleDTO;
import xyz.thewind.music.model.Article;

public interface ArticleService {
    Article addArticle(ArticleDTO articleDTO);
}
