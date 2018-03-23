package garbagecollectors.com.unipool;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by shaloin on 17/3/18.
 */

public class AppStatus extends Thread
{
    private Context context;
    private static int i = 0;

    public AppStatus(Context context){
        this.context=context;
    }

    @Override
    public void run()
    {
        i++;

        Runtime runtime=Runtime.getRuntime();
        try
        {
            Process ipProcess=runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue=ipProcess.waitFor();

            if(exitValue != 0)
            {
                if(i%3 == 0)
                    Toast.makeText(context, "Weak internet connection, be careful out there...", Toast.LENGTH_LONG).show();

                else if(i%3 == 1)
                    Toast.makeText(context, "Weak internet connection, are you in a lift?", Toast.LENGTH_LONG).show();

                else
                    Toast.makeText(context, "Weak internet connection, get out of whatever cave you're in...",Toast.LENGTH_LONG).show();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
