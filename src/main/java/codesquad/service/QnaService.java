package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.swing.text.html.Option;

import codesquad.CannotFoundException;
import codesquad.CannotUpdateException;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return Optional.ofNullable(questionRepository.findOne(id)).orElseThrow(CannotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = Optional.ofNullable(questionRepository.findOne(id)).orElseThrow(CannotUpdateException::new);

        if (question.isOwner(loginUser)) {
            question.update(updatedQuestion);
            return questionRepository.save(question);
        }

        throw new CannotUpdateException();
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question question = findById(questionId);

        if (question.isOwner(loginUser))
            questionRepository.delete(questionId);
        else throw new CannotDeleteException();
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
