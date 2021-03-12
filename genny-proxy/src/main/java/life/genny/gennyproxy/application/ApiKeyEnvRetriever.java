package life.genny.gennyproxy.application;

import io.netty.util.internal.StringUtil;
import org.apache.commons.io.FileUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@ApplicationScoped
@Named("byEnv")
public class ApiKeyEnvRetriever implements IApiKeyRetriever {

    private static final String BASE_DIR ="/tmp/";

    private static final Predicate<String> isNotEmpty = str -> !str.isEmpty();

    private final static Function<String, String> readKeyFromFileSystem = fName -> {
        try {
            File file = new File(BASE_DIR + fName);
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return StringUtil.EMPTY_STRING;
    };

    @Override
    public String retrieveApiKey(String name, String defaultName){
        return Optional.ofNullable(System.getenv(name))
                .filter(Objects::nonNull)
                .filter(isNotEmpty)
                .orElse(readKeyFromFileSystem.apply(defaultName));
    }

    @Override
    public String getSource() {
        return "From File";
    }
}
