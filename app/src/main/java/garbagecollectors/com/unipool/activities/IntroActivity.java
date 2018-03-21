package garbagecollectors.com.unipool.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Note here that we DO NOT use setContentView();

		String uri = "@drawable/home_ss";
		int imageResource = getResources().getIdentifier(uri, null, getPackageName());
		String description = "This is where you'll find other people's entries. You can search for the entry you want to join. " +
								"If you don't find it, you can create your own new one, so that other people can join you.";

		// Instead of fragments, you can also use our default slide
		// Just set a title, description, background and image. AppIntro will do the rest.
		addSlide(AppIntroFragment.newInstance("Home Screen", description, imageResource,Color.parseColor("#3F51B5") ));

	}

	@Override
	public void onSkipPressed(Fragment currentFragment)
	{
		super.onSkipPressed(currentFragment);
		// Do something when users tap on Skip button.
		finish();
	}

	@Override
	public void onDonePressed(Fragment currentFragment)
	{
		super.onDonePressed(currentFragment);
		// Do something when users tap on Done button.
		finish();
	}

	@Override
	public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment)
	{
		super.onSlideChanged(oldFragment, newFragment);
		// Do something when the slide changes.
	}
}
