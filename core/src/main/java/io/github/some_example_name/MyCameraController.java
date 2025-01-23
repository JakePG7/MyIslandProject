/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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
 ******************************************************************************/

package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
// Entirely based on FirstPersonCameraController Class, but I made some edits :)
public class MyCameraController extends InputAdapter {
    protected final Camera camera;
    protected final IntIntMap keys = new IntIntMap();
    public int strafeLeftKey = Keys.A;
    public int strafeRightKey = Keys.D;
    public int forwardKey = Keys.W;
    public int backwardKey = Keys.S;
    public int upKey = Keys.Q;
    public int downKey = Keys.E;
    public boolean autoUpdate = true;
    protected float velocity = 20;
    protected float degreesPerPixel = 0.5f;
    protected final Vector3 tmp = new Vector3();
    protected final Vector3 cameraPosition = new Vector3();
    protected final Vector3 cameraLookAt = new Vector3();
    protected final EditorWorldManager worldManager;



    public MyCameraController (Camera camera, EditorWorldManager worldManager, float cameraX, float cameraY, float cameraZ, float lookAtX, float lookAtY, float lookAtZ) {
        this.camera = camera;
        this.worldManager = worldManager;
        cameraLookAt.set(lookAtX, lookAtY, lookAtZ);
        cameraPosition.set(cameraX, cameraY, cameraZ);
        camera.position.set(cameraPosition);
        camera.lookAt(cameraLookAt);

    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    /** Sets the velocity in units per second for moving forward, backward and strafing left/right.
     * @param velocity the velocity in units per second */
    public void setVelocity (float velocity) {
        this.velocity = velocity;
    }

    /** Sets how many degrees to rotate per pixel the mouse moved.
     * @param degreesPerPixel */
    public void setDegreesPerPixel (float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
        // camera.direction.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        // camera.direction.rotate(tmp, deltaY);
        return true;  // because that's not what I want
        //return false;
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        if (keys.containsKey(forwardKey)) {
            if (camera.position.z < worldManager.getTileTerrainDepth() - 50) {
                camera.position.add(0, 0, 1);

            }

            //tmp.set(cameraPosition);
            //tmp.set(camera.direction).nor().scl(deltaTime * velocity);
            //camera.position.add(tmp);
        }
        if (keys.containsKey(backwardKey)) {
            if (camera.position.z > -20) {
                camera.position.add(0, 0, -1);
            }
            //tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
            //camera.position.add(tmp);
        }
        if (keys.containsKey(strafeLeftKey)) {
            if (camera.position.x < worldManager.getTileTerrainWidth() - 20) {
                camera.position.add(1, 0, 0);
            }
            //tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
            //camera.position.add(tmp);
        }
        if (keys.containsKey(strafeRightKey)) {
            if (camera.position.x > 20) {
                camera.position.add(-1, 0, 0);
            }
            //tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
            //camera.position.add(tmp);
        }
        if (keys.containsKey(upKey)) {
            if (camera.position.y < 85) {
                camera.position.add(0, 1, 0);
            }
        }
        if (keys.containsKey(downKey)) {
            if (camera.position.y > 20) {
                camera.position.add(0, -1, 0);
            }
        }
        if (autoUpdate) camera.update(true);
    }
}
