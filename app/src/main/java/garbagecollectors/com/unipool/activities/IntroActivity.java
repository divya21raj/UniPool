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

		String uriHome = "@drawable/home_ss";
		String uriRequests = "@drawable/requests_ss";
		String uriChat = "@drawable/chat_ss";

		String colorString = "#2467b3";

		int imageResourceHome = getResources().getIdentifier(uriHome, null, getPackageName());
		int imageResourceRequests = getResources().getIdentifier(uriRequests, null, getPackageName());
		int imageResourceChat = getResources().getIdentifier(uriChat, null, getPackageName());

		String descriptionHome = "This is where you'll find other people's entries. You can search for the entry you want to join. " +
				"If you don't find it, you can create your own new one, so that other people can join you.";
		String descriptionRequests = "Here you'll see the received and sent requests. You can accept any request you wish.";
		String descriptionChat = "When you accept someone's request, he/she will show up in your chat.";

		// Instead of fragments, you can also use our default slide
		// Just set a title, description, background and image. AppIntro will do the rest.
		addSlide(AppIntroFragment.newInstance("Home Screen", descriptionHome, imageResourceHome,Color.parseColor(colorString) ));
		addSlide(AppIntroFragment.newInstance("Requests Screen", descriptionRequests, imageResourceRequests,Color.parseColor(colorString) ));
		addSlide(AppIntroFragment.newInstance("Chat Screen", descriptionChat, imageResourceChat,Color.parseColor(colorString) ));
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
