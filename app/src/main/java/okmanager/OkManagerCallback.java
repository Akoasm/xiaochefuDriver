package okmanager;
import android.content.Context;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public abstract class OkManagerCallback extends StringCallback {
//    private MakeLoading dialog;

    public OkManagerCallback() {
    }
    public OkManagerCallback(Context context) {
        try {
//            dialog = new MakeLoading(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(String response, int id) {

    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
//        if (dialog != null && !dialog.isShowing()) {
//            dialog.show();
//        }
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
    }
}
