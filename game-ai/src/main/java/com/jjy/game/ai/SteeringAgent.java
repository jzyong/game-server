package com.jjy.game.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

//A simple steering agent for 2D.
//Of course, for 3D (well, actually for 2.5D) you have to replace all occurrences of Vector2 with  Vector3.
public class SteeringAgent implements Steerable<Vector2> {

	private static final SteeringAcceleration<Vector2> steeringOutput = 
		new SteeringAcceleration<Vector2>(new Vector2());

	Vector2 position;
	float orientation;
	Vector2 linearVelocity;
	float angularVelocity;
	float maxSpeed;
	boolean independentFacing;
	SteeringBehavior<Vector2> steeringBehavior;

	/* Here you should implement missing methods inherited from Steerable */

	// Actual implementation depends on your coordinate system.
	// Here we assume the y-axis is pointing upwards.
	@Override
	public float vectorToAngle (Vector2 vector) {
		return (float)Math.atan2(-vector.x, vector.y);
	}

	// Actual implementation depends on your coordinate system.
	// Here we assume the y-axis is pointing upwards.
	@Override
	public Vector2 angleToVector (Vector2 outVector, float angle) {
		outVector.x = -(float)Math.sin(angle);
		outVector.y = (float)Math.cos(angle);
		return outVector;
	}

	public void update (float delta) {
		if (steeringBehavior != null) {
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);

			/*
			 * Here you might want to add a motor control layer filtering steering accelerations.
			 * 
			 * For instance, a car in a driving game has physical constraints on its movement:
			 * - it cannot turn while stationary
			 * - the faster it moves, the slower it can turn (without going into a skid)
			 * - it can brake much more quickly than it can accelerate
			 * - it only moves in the direction it is facing (ignoring power slides)
			 */

			// Apply steering acceleration to move this agent
			applySteering(steeringOutput, delta);
		}
	}

	private void applySteering (SteeringAcceleration<Vector2> steering, float time) {
		// Update position and linear velocity. Velocity is trimmed to maximum speed
		this.position.mulAdd(linearVelocity, time);
		this.linearVelocity.mulAdd(steering.linear, time).limit(this.getMaxLinearSpeed());

		// Update orientation and angular velocity
		if (independentFacing) {
			this.orientation += angularVelocity * time;
			this.angularVelocity += steering.angular * time;
		} else {
			// For non-independent facing we have to align orientation to linear velocity
//			float newOrientation = calculateOrientationFromLinearVelocity(this);
			float newOrientation=0;
			if (newOrientation != this.orientation) {
				this.angularVelocity = (newOrientation - this.orientation) * time;
				this.orientation = newOrientation;
			}
		}
	}

	@Override
	public Vector2 getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getOrientation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOrientation(float orientation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location<Vector2> newLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxLinearSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxLinearAcceleration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxAngularSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxAngularAcceleration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector2 getLinearVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getAngularVelocity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getBoundingRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTagged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTagged(boolean tagged) {
		// TODO Auto-generated method stub
		
	}
}