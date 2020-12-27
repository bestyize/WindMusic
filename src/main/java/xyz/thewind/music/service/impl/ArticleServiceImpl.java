package xyz.thewind.music.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.thewind.music.dto.ArticleDTO;
import xyz.thewind.music.dto.DTOConverter;
import xyz.thewind.music.model.Article;
import xyz.thewind.music.repository.ArticleRepository;
import xyz.thewind.music.service.ArticleService;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    ArticleRepository articleRepository;
    @Override
    public Article addArticle(ArticleDTO articleDTO) {
        Article article= DTOConverter.convertArticleDtoToArticle(articleDTO);
        return articleRepository.save(article);
    }

    @Override
    public Article findArticleByArticleId(String articleId) {
        return articleRepository.findTopByArticleId(articleId);
    }

    @Override
    public List<Article> findArticleByUserId(String userId) {
        return articleRepository.findArticlesByUserId(userId);
    }
}
