package act.handler.builtin;

import act.app.App;
import act.controller.Controller;
import act.controller.ParamNames;
import act.handler.builtin.controller.FastRequestHandler;
import org.osgl.http.H;
import org.osgl.mvc.result.NotFound;
import act.app.AppContext;
import org.osgl.util.E;
import org.osgl.util.FastStr;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static act.controller.Controller.Util.*;

public class StaticFileGetter extends FastRequestHandler {
    private File base;

    public StaticFileGetter(String base, App app) {
        E.NPE(base);
        this.base = app.file(base);
    }

    public StaticFileGetter(File base) {
        E.NPE(base);
        this.base = base;
    }

    @Override
    public void handle(AppContext context) {
        File file = base;
        if (!file.exists()) {
            AlwaysNotFound.INSTANCE.handle(context);
            return;
        }
        H.Format fmt;
        if (base.isDirectory()) {
            String path = context.param(ParamNames.PATH);
            if (S.blank(path)) {
                AlwaysBadRequest.INSTANCE.handle(context);
                return;
            }
            fmt = contentType(path);
            file = new File(base, path);
            if (!file.exists()) {
                AlwaysNotFound.INSTANCE.handle(context);
                return;
            }
            if (!file.canRead()) {
                AlwaysForbidden.INSTANCE.handle(context);
                return;
            }
        } else {
            fmt = contentType(file.getPath());
        }
        InputStream is = new BufferedInputStream(IO.is(file));
        H.Response resp = context.resp();
        if (H.Format.unknown != fmt) {
            resp.contentType(fmt.toContentType());
        }
        IO.copy(is, context.resp().outputStream());
    }

    // for unit test
    public File base() {
        return base;
    }

    private H.Format contentType(String path) {
        FastStr s = FastStr.unsafeOf(path).afterLast('.');
        return H.Format.valueOfIgnoreCase(s.toString());
    }

    protected InputStream inputStream(String path, AppContext context) {
        return context.app().classLoader().getResourceAsStream(path);
    }

    @Override
    public boolean supportPartialPath() {
    return base.isDirectory();
    }

}
