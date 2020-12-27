package xyz.thewind.music.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import xyz.thewind.music.dto.ArticleDTO;
import xyz.thewind.music.dto.DTOConverter;
import xyz.thewind.music.model.Article;
import xyz.thewind.music.repository.ArticleRepository;
import xyz.thewind.music.service.ArticleService;

public class ArticleServiceImpl implements ArticleService {
    @Autowired
    ArticleRepository articleRepository;
    @Override
    public Article addArticle(ArticleDTO articleDTO) {
        Article article= DTOConverter.convertArticleDtoToArticle(articleDTO);
        return articleRepository.save(article);
    }
}
