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

		String uriWelcome = "@drawable/logo";
		String uriHome = "@drawable/home_ss";
		String uriRequests = "@drawable/requests_ss";
		String uriChat = "@drawable/chat_ss";
		String uriNewEntry = "@drawable/new_entry_ss";

		String colorString = "#2467b3";

		int imageResourceWelcome = getResources().getIdentifier(uriWelcome, null, getPackageName());
		int imageResourceHome = getResources().getIdentifier(uriHome, null, getPackageName());
		int imageResourceRequests = getResources().getIdentifier(uriRequests, null, getPackageName());
		int imageResourceChat = getResources().getIdentifier(uriChat, null, getPackageName());
		int imageResourceNewEntry = getResources().getIdentifier(uriNewEntry, null, getPackageName());

		String descriptionWelcome = "Thanks for downloading! Let's see how stuff works here...";
		String descriptionHome = "This is where you search for your desired entry and send a request to join them";
		String descriptionNewEntry = "If you don't find a desired entry, you can create your own by pressing " +
										"the cab button and wait for someone to join you...";
		String descriptionRequests = "Here you'll see the requests you've sent and received";
		String descriptionChat = "When you accept someone's request, or someone else does yours, they will show up here";
		String descriptionDone = "You're all set!";

		// Instead of fragments, you can also use our default slide
		// Just set a titleString, description, background and image. AppIntro will do the rest.
		addSlide(AppIntroFragment.newInstance("Welcome!", descriptionWelcome, imageResourceWelcome,Color.parseColor(colorString) ));
		addSlide(AppIntroFragment.newInstance("Home", descriptionHome, imageResourceHome,Color.parseColor(colorString) ));
		addSlide(AppIntroFragment.newInstance("New Entry", descriptionNewEntry, imageResourceNewEntry,Color.parseColor(colorString) ));
		addSlide(AppIntroFragment.newInstance("Requests", descriptionRequests, imageResourceRequests,Color.parseColor(colorString)));
		addSlide(AppIntroFragment.newInstance("Chat", descriptionChat, imageResourceChat,Color.parseColor(colorString) ));
		addSlide(AppIntroFragment.newInstance("Done!", descriptionDone, imageResourceWelcome,Color.parseColor(colorString) ));
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
