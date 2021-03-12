package test.component.life.gennyproxy;

import io.quarkus.test.junit.QuarkusTest;
import life.genny.gennyproxy.repository.entity.abn.AbnSearchResult;
import life.genny.gennyproxy.service.AbnLookupService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class AbnLookupServiceTest {


    @Inject
    AbnLookupService abnLookupService;

    @Test
    public void retrieveCompanyAbn_passValidParameter_return200() throws  Exception {
        AbnSearchResult abnSearchResult = abnLookupService.retrieveCompanyAbn("admin", "outcome", 20);
        System.out.println(abnSearchResult);
        System.out.println(abnLookupService.getSource());
    }
}
