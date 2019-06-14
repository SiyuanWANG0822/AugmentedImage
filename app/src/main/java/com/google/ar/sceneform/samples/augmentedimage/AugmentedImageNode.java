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
 * A Node represents a transformation within the scene graph's hierarchy.
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

  // The augmented image represented by this node.
  private AugmentedImage image;

  //为了确保3D模型构建完成的一个异步计算
  // We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
  private static CompletableFuture<ModelRenderable> ulCorner;

  //实现Activity类和Node类的交互
  private AugmentedImageActivity context;

  //构造函数
  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
    this.context = (AugmentedImageActivity) context;

    //构建3D模型
    if (ulCorner == null) {
      ulCorner =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("IronMan.sfb"))
                      .build();
    }
  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The renderables are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the renderables.
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

    //得到Activity类中的arFragment参数
    ArFragment arFragment = context.getArFragment();

    //Make the renderable node.
    //Sets the position of this node relative to its parent (local-space).
    Vector3 localPosition = new Vector3();
    localPosition.set(0.0f, 0.0f, 0.0f);

    //set the position of the anchor
    this.setAnchor(image.createAnchor(image.getCenterPose()));

    /**
    //AnchorNode cornerNode = new AnchorNode(image.createAnchor(image.getCenterPose()));
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(ulCorner.getNow(null));*/

    // Create the transformable renderable and add it to the anchor.
    TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
    transformableNode.setParent(this);
    transformableNode.setLocalPosition(localPosition);
    transformableNode.setRenderable(ulCorner.getNow(null));
    transformableNode.select();



    //set rotation in direction (x,y,z) in degrees 90
    transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), -90f));
    transformableNode.setParent(this);
    transformableNode.setRenderable(ulCorner.getNow(null));
    transformableNode.select();
  }

  public AugmentedImage getImage() {
    return image;
  }

}