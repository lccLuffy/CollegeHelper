package com.lcc.uestc.data;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.lcc.uestc.Config;
import com.lcc.uestc.bean.ExamInfoBean;
import com.lcc.uestc.bean.GradeBean;
import com.lcc.uestc.bean.UestcNewsBean;
import com.lcc.uestc.bean.User;
import com.lcc.uestc.utils.CommonUtils;
import com.lcc.uestc.utils.PreferenceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class UESTC {
    private static Map<String,String> COOKIE_CACHE = new HashMap<>();
    private static String COOKIE_TAG = "iPlanetDirectoryPro";
    public final static Map<Integer,String> SEMESTERS = new ArrayMap<>();
    static
    {
        SEMESTERS.put(43,"2014-2015上学期");
        SEMESTERS.put(63,"2014-2015下学期");
        SEMESTERS.put(84,"2015-2016上学期");
        SEMESTERS.put(103,"2015-2016下学期");
    }

    public static final int EXAM_TYPE_FINAL = 1;			//期末
    public static final int EXAM_TYPE_MID = 2;				//期中
    public static final int EXAM_TYPE_MAKEUP = 3;			//补考
    public static final int EXAM_TYPE_HUANKAO = 4;			//缓考


    public static int DEFAULT_SEMESTER = 84;

    public static abstract class BaseAsyncTask<Result> extends AsyncTask<Void, Void, Result>
    {


        protected String failReason = "fail";
        public final String xueHao;
        public final String password;
        public final CallBack<Result> callBack;
        public BaseAsyncTask(String xueHao, String password, CallBack<Result> callBack) {
            this.xueHao = xueHao;
            this.password = password;
            this.callBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            if(callBack != null)
                callBack.onStart();
        }

        @Override
        protected Result doInBackground(Void... params) {
            return parse(getInfoString(xueHao,password,getUrl()));
        }

        @Override
        protected void onPostExecute(Result result) {
            if(result == null)
            {
                if(callBack != null)
                {
                    callBack.onFailed(failReason);
                }
            }
            else
            {
                if(callBack != null)
                {
                    callBack.onSuccess(result);
                }
            }
        }

        protected abstract Result parse(String html);
        protected abstract String getUrl();
        public String getInfoString(String xueHao, String password,String url) {
            String result = null;
            try {
                String cookie = getCookieString(xueHao, password);
                if(cookie == null)
                    return null;
                Response response = OkHttpUtils.post()
                        .addHeader("Cookie",cookie)
                        .url(url)
                        .build()
                        .execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
    }
    static class LoginAsyncTask extends BaseAsyncTask<JSONObject>
    {
        String url;
        public LoginAsyncTask(String xueHao, String password, CallBack<JSONObject> callBack) {
            super(xueHao,password,callBack);
            url = "http://eams.uestc.edu.cn/eams/stdDetail.action";
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        protected JSONObject parse(String result) {
            JSONObject jsonObject = new JSONObject();
            if(result != null)
            {
                Document doc = Jsoup.parse(result);
                try
                {
                    Element content = doc.getElementById("tabPage1");
                    Elements links = content.getElementsByTag("td");
                    int index = 0;
                    String beforeKey = "";
                    for (int i=0;i<links.size();i++)
                    {
                        String item = links.get(i).text();

                        if(index % 2 == 1)
                        {
                            beforeKey = item;
                        }
                        else if(index != 0)
                        {
                            jsonObject.put(beforeKey, item);
                        }
                        if(!item.trim().equals(""))
                        {
                            index++;
                        }
                    }
                }
                catch(Exception e)
                {
                    return null;
                }
                return jsonObject;
            }
            return null;
        }
    }

    static class ExamAsyncTask  extends BaseAsyncTask<List<ExamInfoBean>>
    {
        public final int semester;
        public final int examType;
        String url;
        public ExamAsyncTask(String xueHao, String password, CallBack<List<ExamInfoBean>> callBack) {
            super(xueHao,password,callBack);
            semester = PreferenceUtils.getInstance().getIntParam(PreferenceUtils.SEMESTER_KEY_EXAM,DEFAULT_SEMESTER);
            examType = PreferenceUtils.getInstance().
                    getIntParam(PreferenceUtils.EXAM_TYPE_KEY_EXAM, EXAM_TYPE_FINAL);
            url = "http://eams.uestc.edu.cn/eams/stdExamTable!examTable.action" +
                    "?semester.id="+ semester+"&examType.id="+examType;
        }

        @Override
        public String getUrl() {
            return url;
        }
        @Override
        protected List<ExamInfoBean> parse(String html)
        {
            if(html == null)
                return null;
            boolean first = true;
            List<ExamInfoBean> exams = new ArrayList<ExamInfoBean>();
            Document doc = Jsoup.parse(html);
            Elements tables = doc.getElementsByTag("table");
            for(Element table : tables)
            {
                Elements infos = table.getElementsByTag("tr");
                for(Element info : infos)
                {
                    if(!first)
                    {
                        Elements items = info.getElementsByTag("td");
                        int i = 0;
                        ExamInfoBean examInfoBean = new ExamInfoBean();
                        boolean ok = true;
                        for(Element item : items)
                        {
                            if(!get(examInfoBean,item.text(),i))
                            {
                                ok = false;
                                break;
                            }
                            i++;
                        }
                        if(ok)
                        {
                            examInfoBean.setExamType(examType);
                            examInfoBean.setSemester(semester);
                            exams.add(examInfoBean);
                            examInfoBean.save();
                        }
                    }
                    first = false;
                }
            }
            return exams;
        }
        private boolean get(ExamInfoBean examInfoBean,String data,int pos)
        {

            switch (pos)
            {
                case 0:
                    examInfoBean.setCourseNumber(data);
                    break;
                case 1:
                    examInfoBean.setCourseName(data);
                    break;
                case 2:
                    if (data == null || "".equals(data))
                        return false;
                    if (data.startsWith("["))
                    {
                        return false;
                    }
                    examInfoBean.setDate(data.trim());
                    break;
                case 3:
                    examInfoBean.setTime(data.substring(data.indexOf(')')+1).trim());
                    int indexFirstBlack = data.indexOf(' ');
                    examInfoBean.setOrderOfWeek(data.substring(0,indexFirstBlack).trim());
                    examInfoBean.setWeek(data.substring(indexFirstBlack,data.indexOf('(')).trim());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date date = dateFormat.parse(examInfoBean.getDate()+" "+examInfoBean.getTime().substring(0,examInfoBean.getTime().indexOf('-')));
                        examInfoBean.setStartTime(date.getTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                        CommonUtils.i(e.toString());
                    }
                    break;
                case 4:
                    examInfoBean.setPlace(data);
                    break;
                case 5:
                    examInfoBean.setSeat(data);
                    break;
                case 6:
                    examInfoBean.setSituation(data);
                    break;
                case 7:
                    examInfoBean.setOthers(data);
                    break;
            }
            return true;
        }
    }

    static class GradeAsyncTask extends BaseAsyncTask<List<GradeBean>>
    {
        public final int semester;
        String url;
        public GradeAsyncTask(String xueHao, String password,CallBack<List<GradeBean>> callBack) {
            super(xueHao,password,callBack);
            semester = PreferenceUtils.getInstance().getIntParam(PreferenceUtils.SEMESTER_KEY_GRADE, DEFAULT_SEMESTER);
            url = "http://eams.uestc.edu.cn/eams/teach/grade/course/person!search.action?semesterId="+semester;
        }

        @Override
        protected String getUrl() {
            return url;
        }

        @Override
        protected List<GradeBean> parse(String html)
        {
            if(html == null)
                return null;
            boolean first = true;
            List<GradeBean> grades = new ArrayList();
            Document doc = Jsoup.parse(html);
            Elements tables = doc.getElementsByTag("table");
            for(Element table : tables)
            {
                Elements infos = table.getElementsByTag("tr");
                for(Element info : infos)
                {
                    if(!first)
                    {
                        Elements items = info.getElementsByTag("td");
                        if(!items.isEmpty())
                        {
                            int i = 0;
                            GradeBean grade = new GradeBean();
                            for(Element item : items)
                            {
                                String text = item.text();
                                get(grade,text,i);
                                i++;
                            }
                            grade.setSemesterCode(semester);
                            grades.add(grade);
                            grade.save();
                        }
                    }
                    first = false;
                }
            }
            return grades;
        }
        private GradeBean get(GradeBean grade,String s,int pos)
        {
            switch (pos)
            {
                case 0:
                    grade.setSemester(s);
                    break;
                case 1:
                    grade.setCourseCode(s);
                    break;
                case 2:
                    grade.setCourseNumber(s);
                    break;
                case 3:
                    grade.setCourseName(s);
                    break;
                case 4:
                    grade.setCourseType(s);
                    break;
                case 5:
                    grade.setCredit(s);
                    break;
                case 6:
                    grade.setGrade(s);
                    break;
                case 7:
                    grade.setMakeupGrade(s);
                    break;
                case 8:
                    grade.setFinalGrade(s);
                    break;
                case 9:
                    grade.setGPA(s);
                    break;
            }
            return grade;
        }
    }

    public static void getStudentInfo(final String xueHao,final String password,final CallBack<JSONObject> callBack)
    {
        new LoginAsyncTask(xueHao,password,callBack).execute();
    }
    public static void getExamInfo(CallBack<List<ExamInfoBean>> callBack)
    {
        String xueHao = User.getInstance().getXuehao();
        String password = User.getInstance().getPassword();
        if (TextUtils.isEmpty(xueHao) || TextUtils.isEmpty(password)) {
            EventBus.getDefault().post(Config.NO_LOGIN_EVENT);
            return;
        }

        int examType = PreferenceUtils.getInstance()
                .getIntParam(PreferenceUtils.EXAM_TYPE_KEY_EXAM, EXAM_TYPE_FINAL);
        int semester = PreferenceUtils.getInstance()
                .getIntParam(PreferenceUtils.SEMESTER_KEY_EXAM, DEFAULT_SEMESTER);
        List<ExamInfoBean> list = ExamInfoBean.find(ExamInfoBean.class,"EXAM_TYPE = ? and SEMESTER = ?",""+examType,""+semester);

        if (list.isEmpty())
        {
            new ExamAsyncTask(xueHao, password, callBack).execute();
        }
        else if (callBack != null) {
            callBack.onSuccess(list);
        }
    }
    public static void getGradeInfo(CallBack<List<GradeBean>> callBack)
    {
        String xueHao = User.getInstance().getXuehao();
        String password = User.getInstance().getPassword();
        if (TextUtils.isEmpty(xueHao) || TextUtils.isEmpty(password)) {
            EventBus.getDefault().post(Config.NO_LOGIN_EVENT);
            return;
        }
        int semesterCode = PreferenceUtils.
                getInstance().getIntParam(PreferenceUtils.SEMESTER_KEY_GRADE,DEFAULT_SEMESTER);
        List<GradeBean> list = GradeBean.find(GradeBean.class,"SEMESTER_CODE=?",""+semesterCode);
        if (list.isEmpty())
        {
            new GradeAsyncTask(xueHao,password,callBack).execute();
        }
        else if (callBack != null) {
            callBack.onSuccess(list);
        }
    }
    public static void getNews(int type,int page,final CallBack<List<UestcNewsBean>> callBack)
    {
        String url = "http://www.new1.uestc.edu.cn/?n=UestcNews.Front.Category.Page&CatId="+type+"&page="+page;
        final List<UestcNewsBean> list = new ArrayList<UestcNewsBean>();

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (callBack != null) {
                            callBack.onFailed(e.toString());
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        if(TextUtils.isEmpty(response))
                        {
                            if (callBack != null) {
                                callBack.onFailed("Sorry,failed to get information from Uestc website");
                            }
                            return;
                        }
                        Document doc = Jsoup.parse(response);
                        Elements mElements = doc.getElementsByAttributeValue("id", "Degas_news_list");
                        if (mElements == null)
                        {
                            if (callBack != null) {
                                callBack.onFailed("Sorry,failed to get information from Uestc website");
                            }
                            return;
                        }
                        for (Element mElement: mElements)
                        {
                            Elements ListTitle = mElement.getElementsByTag("h3");
                            Elements ListContent = mElement.getElementsByAttributeValue("class", "desc");
                            Elements ListTime = mElement.getElementsByAttributeValue("class", "time");
                            Elements pics = mElement.getElementsByTag("img");
                            for (int i = 0; i < ListTitle.size() && i < ListContent.size() && i < ListTime.size() && i < pics.size(); i++)
                            {
                                String title = ListTitle.get(i).text();
                                String content = ListContent.get(i).text();
                                String web = ListTitle.get(i).getElementsByTag("a").attr("href");
                                String time = ListTime.get(i).text();
                                String pic = pics.get(i).attr("src");
                                web = "http://www.new1.uestc.edu.cn/" + web;
                                UestcNewsBean news = new UestcNewsBean();
                                news.setTitle(title);
                                news.setContent(content);
                                news.setUrl(web);
                                news.setPic("http://www.new1.uestc.edu.cn/" + pic);
                                news.setTime(time);
                                list.add(news);
                            }
                        }

                        if (callBack != null)
                        {
                            callBack.onSuccess(list);
                        }
                    }
                });
    }



    public static String getCookieString(String xueHao,String password)
    {
        String cookie = COOKIE_CACHE.get(xueHao);
        if (cookie != null)
            return cookie;

        Response response;
        try {
            response = OkHttpUtils.post()
                    .url("https://uis.uestc.edu.cn/amserver/UI/Login?goto=http%3A%2F%2Fportal.uestc.edu.cn%2Flogin.portal")
                    .addParams("IDButton", "Submit")
                    .addParams("IDToken1",xueHao)
                    .addParams("IDToken2",password)
                    .addParams("encoded", "true")
                    .addParams("gx_charset", "UTF-8")
                    .build()
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        List<String> ls = response.headers("Set-cookie");
        for(String v: ls)
        {
            if(v.startsWith(COOKIE_TAG)) {
                cookie = v;
                COOKIE_CACHE.put(xueHao,v);
            }
        }
        return cookie;
    }
}
