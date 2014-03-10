package eoc.studio.voicecard.newspaper;

import eoc.studio.voicecard.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class NewspaperMainActivity extends Activity implements OnClickListener
{
    private static ImageButton mBtnNewspaperType, mBtnMagazineType;
    private static Button btnBackMainMenu;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_newspaper_main_view);
        findView();
        buttonFunction();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findView()
    {
        mBtnNewspaperType = (ImageButton) findViewById(R.id.newspaperImageButton);
        mBtnMagazineType = (ImageButton) findViewById(R.id.magazineImageButton);
        btnBackMainMenu = (Button) findViewById(R.id.btnBackMainMenu);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void buttonFunction()
    {
        mBtnNewspaperType.setOnClickListener(this);
        mBtnMagazineType.setOnClickListener(this);
        btnBackMainMenu.setOnClickListener(this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.newspaperImageButton:
            {
                ValueCacheProcessCenter.selectedStyleType = ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER;
                runNewspaperEditActivity();
            }
                break;
            case R.id.magazineImageButton:
            {
                ValueCacheProcessCenter.selectedStyleType = ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE;
                runMagazineEditActivity();
            }
                break;
            case R.id.btnBackMainMenu:
            {
                finish();
            }
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void runNewspaperEditActivity()
    {
        Intent editFunction = new Intent(this, NewspaperStyleMainActivity.class);
        startActivity(editFunction);
        overridePendingTransition(0, 0);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void runMagazineEditActivity()
    {
        Intent editFunction = new Intent(this, MagazineStyleMainActivity.class);
        startActivity(editFunction);
        overridePendingTransition(0, 0);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
