package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "김민규")
                .addParameter("contents", "김민규")
                .build();

        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/");
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 자신이_작성한_질문일때_수정_가능() throws Exception {
        User user = defaultUser();
        ResponseEntity<String> response = update(basicAuthTemplate(user));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    @Test
    public void 자신이_작성한_질문이_아닌데_수정할_경우_예외발생() throws Exception {
        User user = findByUserId("sanjigi");
        ResponseEntity<String> response = update(basicAuthTemplate(user));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void 자신이_작성한_질문이_아닌데_삭제할_경우_예외발생() throws Exception {
        User user = findByUserId("sanjigi");
        ResponseEntity<String> response = delete(basicAuthTemplate(user));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "업데이트 성공")
                .addParameter("contents", "업데이트 성공!")
                .build();

        saveQuestion();
        return template.postForEntity("/questions/1", request, String.class);
    }

    private ResponseEntity<String> delete(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        saveQuestion();
        return template.postForEntity("/questions/1", request, String.class);
    }

    private void saveQuestion() {
        Question question = new Question("안녕~", "안녕ㅎㅎ");
        question.writeBy(defaultUser());

        questionRepository.save(question);
    }
}
