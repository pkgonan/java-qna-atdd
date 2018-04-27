package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.CannotFoundException;
import codesquad.CannotUpdateException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("question", qnaService.findAll());
        return "home";
    }

    @GetMapping("/{id}")
    public String get(@PathVariable long id, Model model) throws CannotFoundException {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, QuestionDto question) {
        Question createdQuestion = qnaService.create(loginUser, question);
        return String.format("redirect:%s", createdQuestion.generateUrl());
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto target) {
        Question updateQuestion = qnaService.update(loginUser, id, target.toQuestion());
        return String.format("redirect:%s", updateQuestion.generateUrl());
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);
        return "home";
    }
}
