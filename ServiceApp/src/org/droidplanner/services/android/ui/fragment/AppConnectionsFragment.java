package org.droidplanner.services.android.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.droidplanner.services.android.R;
import org.droidplanner.services.android.api.DroidPlannerService;
import org.droidplanner.services.android.api.DroneAccess;
import org.droidplanner.services.android.api.DroneApi;
import org.droidplanner.services.android.ui.activity.MainActivity;
import org.droidplanner.services.android.ui.adapter.AppConnectionAdapter;

import java.util.List;

/**
 * Created by fhuya on 12/15/14.
 */
public class AppConnectionsFragment extends Fragment {

    private final static IntentFilter intentFilter = new IntentFilter();

    static {
        intentFilter.addAction(DroidPlannerService.ACTION_DRONE_CREATED);
        intentFilter.addAction(DroidPlannerService.ACTION_DRONE_DESTROYED);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (DroidPlannerService.ACTION_DRONE_CREATED.equals(action)
                    || DroidPlannerService.ACTION_DRONE_DESTROYED.equals(action)) {
                refreshDroneList();
            }
        }
    };

    private MainActivity parent;

    private LocalBroadcastManager lbm;

    private AppConnectionAdapter appConnectionAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(!(activity instanceof MainActivity)){
            throw new IllegalStateException("Parent must be an instance of " + MainActivity.class.getName());
        }

        parent = (MainActivity) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        parent = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_app_connections, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        final Context context = getActivity().getApplicationContext();
        lbm = LocalBroadcastManager.getInstance(context);

        recyclerView = (RecyclerView) view.findViewById(R.id.app_connections_list);

        //Use this setting to improve performance if you know that changes in content do not change the layout side
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);

        //Use a grid layout manager
        final int colCount = getResources().getInteger(R.integer.connectionsColCount);
        final RecyclerView.LayoutManager gridLayoutMgr = new GridLayoutManager(context, colCount);
        recyclerView.setLayoutManager(gridLayoutMgr);

        appConnectionAdapter = new AppConnectionAdapter(context);
        recyclerView.setAdapter(appConnectionAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        lbm.registerReceiver(broadcastReceiver, intentFilter);
        refreshDroneList();
    }

    @Override
    public void onStop(){
        super.onStop();
        lbm.unregisterReceiver(broadcastReceiver);
    }

    public void refreshDroneList(){
        DroneAccess droneAccess = parent.getDroneAccess();
        if(droneAccess != null) {
            List<DroneApi> dronesList = droneAccess.getDroneApiList();
            appConnectionAdapter.refreshDroneManagerList(dronesList);
        }
    }
}
