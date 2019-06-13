/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

  // The augmented image represented by this node.
  private AugmentedImage image;
  private Renderable renderable;


  // Models of the 4 corners.  We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
  private static CompletableFuture<ModelRenderable> ulCorner;

  private AugmentedImageActivity context;


  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
      this.context = (AugmentedImageActivity) context;
    if (ulCorner == null) {
      ulCorner =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("12228_Dog_v1_L2.sfb"))
                      .build();
    }
  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The corners are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corners.
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!ulCorner.isDone()) {
      CompletableFuture.allOf(ulCorner)
              .thenAccept((Void aVoid) -> setImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });

    }

      ArFragment arFragment = context.getArFragment();
      //Make the 4 corner nodes.
      Vector3 localPosition = new Vector3();
      // Upper left corner.
      localPosition.set(0.0f, 0.0f, 0.0f);






      /**arFragment.setOnTapArPlaneListener(


              (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                  if (ulCorner.getNow(null) == null) {
                      return;
                  }
                  // Create the Anchor.
                  Anchor anchor = hitResult.createAnchor();
                  AnchorNode anchorNode = new AnchorNode(anchor);
                  anchorNode.setParent(arFragment.getArSceneView().getScene());

                  // Create the transformable andy and add it to the anchor.
                  TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                  andy.setParent(anchorNode);
                  andy.setRenderable(ulCorner.getNow(null));
                  andy.select();});*/


      this.setAnchor(image.createAnchor(image.getCenterPose()));
      //AnchorNode cornerNode = new AnchorNode(image.createAnchor(image.getCenterPose()));
      /**


      cornerNode.setParent(this);
      cornerNode.setLocalPosition(localPosition);
      cornerNode.setRenderable(ulCorner.getNow(null));*/

      // Create the transformable andy and add it to the anchor.
      TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
      transformableNode.setParent(this);
      transformableNode.setLocalPosition(localPosition);
      transformableNode.setRenderable(ulCorner.getNow(null));
      transformableNode.select();



      //set rotation in direction (x,y,z) in degrees 90
      transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 180f));
      transformableNode.setParent(this);
      transformableNode.setRenderable(ulCorner.getNow(null));
      transformableNode.select();






  }










      /**

      //set rotation in direction (x,y,z) in degrees 90
      transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), 180f));
      transformableNode.setParent(anchorNode);
      transformableNode.setRenderable(node.getMyRenderable());
      transformableNode.select();*/






  public AugmentedImage getImage() {
    return image;
  }


}