package com.lcc.uestc.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lcc.uestc.R;
import com.lcc.uestc.bean.CodeBean;
import com.lcc.uestc.utils.ActivityJumpUtils;
import com.lcc.uestc.utils.CommonUtils;
import com.lcc.uestc.widget.Recycler.NiceAdapter;
import com.lcc.uestc.widget.Recycler.NiceViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lcc_luffy on 2016/1/9.
 */
public class CodeAdapter extends NiceAdapter<CodeBean> {
    public CodeAdapter(final Activity context) {
        super(context);
        setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(int mItem) {
                ActivityJumpUtils.toWebView(context,data.get(mItem).getUrl());
            }
        });
    }



    public List<CodeBean>getData()
    {
        return data;
    }

    @Override
    protected NiceViewHolder onCreateNiceViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.item_code,parent,false));
    }

    class Holder extends NiceViewHolder<CodeBean>
    {
        @Bind(R.id.code_imageView)
        SimpleDraweeView imageView;

        @Bind(R.id.code_description)
        TextView description;

        @Bind(R.id.code_title)
        TextView title;

        @Bind(R.id.star)
        ImageView star;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        @Override
        public void onBindData(final CodeBean codeBean)
        {
            title.setText(codeBean.getTitle());
            description.setText(codeBean.getDescription());
            String url = codeBean.getPicture();
            Uri uri = Uri.parse(url);
            if(url.endsWith(".gif"))
            {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true)
                        .build();
                imageView.setController(controller);
            }
            else
            {
                imageView.setImageURI(uri);
            }
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!codeBean.isCollected()) {
                        codeBean.setCollected(true);
                        star.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_star_checked));
                        BmobQuery<CodeBean> bmobQuery = new BmobQuery<CodeBean>();
                        bmobQuery.addWhereEqualTo("url",codeBean.getUrl());
                        bmobQuery.findObjects(getContext(), new FindListener<CodeBean>() {
                            @Override
                            public void onSuccess(List<CodeBean> list) {
                                if(list.isEmpty())
                                {
                                    collectCode(codeBean);
                                }
                                else
                                {
                                    CommonUtils.toast(codeBean.getTitle()+"已经收藏过了");
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                    else
                    {
                        codeBean.setCollected(false);
                        star.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_star_unchecked));
                    }
                }
            });
        }

    }

    private void collectCode(final CodeBean codeBean)
    {
        codeBean.save(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                CommonUtils.toast(codeBean.getTitle()+" 收藏成功");
                codeBean.setCollected(true);
            }

            @Override
            public void onFailure(int i, String s) {
                CommonUtils.toast(codeBean.getTitle()+" 收藏失败,"+s);
            }
        });
    }
}
