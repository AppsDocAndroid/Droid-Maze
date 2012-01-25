/**
 * Copyright 2011 Massimo Gaddini
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 */

package com.sgxmobileapps.droidmaze.game;

import android.content.Context;

import com.sgxmobileapps.droidmaze.datastore.DroidMazeDbAdapter;
import com.sgxmobileapps.droidmaze.game.model.GamePlayerProfile;
import com.sgxmobileapps.droidmaze.maze.generator.IterativeBacktrackingMazeGenerator;
//import com.sgxmobileapps.droidmaze.maze.generator.KruskalMazeGenerator;
import com.sgxmobileapps.droidmaze.maze.generator.MazeGenerator;

/**
 * TODO
 * @author Massimo Gaddini
 */
public class GameLevelManager {
    
    class Level {
        public int mMazeWidth;
        public int mMazeHeight;
        public float mAccelerationFactor;
        
        public Level(int w, int h, float a){
            mMazeWidth = w;
            mMazeHeight = h;
            mAccelerationFactor = a;
        }
    }
    
    private Level[] mLevels = { new Level(10, 10, 0.2f), 
                                new Level(10, 10, 0.2f),
                                new Level(11, 11, 0.2f) };

    private GamePlayerProfile mGamePlayerProfile;
    private MazeGenerator mGenerator;
    
    private static GameLevelManager mInstance;
    
    public static GameLevelManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new GameLevelManager(context);
        }
        
        return mInstance;
    }

    private GameLevelManager(Context context) {
        /* TODO: load state from db */
        DroidMazeDbAdapter dbAdapter = new DroidMazeDbAdapter(context);
        
        dbAdapter.open();
        GamePlayerProfile[] profiles = dbAdapter.getAllGamePlayerProfile();
        
        mGamePlayerProfile = null;
        for (GamePlayerProfile gp: profiles){
            if (gp.isCurrent()){
                mGamePlayerProfile = gp;
                break;
            }
        }
        
        if (mGamePlayerProfile == null){
            mGamePlayerProfile = new GamePlayerProfile();
            mGamePlayerProfile.setProfileId("default_user");
            mGamePlayerProfile.setLevel(1);
            mGamePlayerProfile.setTotalTime(0);
            mGamePlayerProfile.setCurrent(true);
            dbAdapter.addGamePlayerProfile(mGamePlayerProfile);
        }
        
        dbAdapter.close();
        
        mGenerator = new IterativeBacktrackingMazeGenerator();
    }

    /**
     * @return the nickname
     */
    public String getNickname() {
        return mGamePlayerProfile.getProfileId();
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return mGamePlayerProfile.getLevel();
    }
    
    /**
     * @return the total time
     */
    public long getTotalTime() {
        return mGamePlayerProfile.getTotalTime();
    }

    /**
     * @return the generator
     */
    public MazeGenerator getGenerator() {
        return mGenerator;
    }
    
    /**
     * @return the mazeWidth
     */
    public int getMazeWidth() {
        return mLevels[mGamePlayerProfile.getLevel() - 1].mMazeWidth;
    }

    /**
     * @return the mazeHeight
     */
    public int getMazeHeight() {
        return mLevels[mGamePlayerProfile.getLevel() - 1].mMazeHeight;
    }

    /**
     * @return the accelerationFactor
     */
    public float getAccelerationFactor() {
        return mLevels[mGamePlayerProfile.getLevel() - 1].mAccelerationFactor;
    }
}