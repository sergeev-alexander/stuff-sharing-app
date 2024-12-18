package alexander.sergeev.stuff_sharing_app.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/test")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TestController {

    private final TestClient testClient;

    @GetMapping
    public ResponseEntity<Object> getTest() {
        return testClient.getTest();
    }
}