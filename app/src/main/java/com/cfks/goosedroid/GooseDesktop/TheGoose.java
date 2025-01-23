/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid.GooseDesktop;

import android.content.*;
import android.graphics.*;
import com.cfks.goosedroid.*;
import com.cfks.goosedroid.SamEngine.*;
import java.util.*;
import jiesheng.*;

public class TheGoose {
    private static Bitmap ShadowBitmap;
    private static  Paint ShadowPaint;
    private static Paint ShadowPen;
    private static Paint DrawingPen;
	private static Canvas canvas;
	private static ConfigureActivity ca;
	private static Context ctx;

    public static void Init(Context ctx,Canvas canvas,ConfigureActivity ca) {
		TheGoose.canvas = canvas;
		TheGoose.ctx = ctx;
		TheGoose.ca = ca;
		TheGoose.position = new Vector2(TheGoose.canvas.getWidth() / 2,TheGoose.canvas.getHeight() / 2);//new Vector2(-20f, 120f);
		TheGoose.targetPos = new Vector2(100f, 150f);
		Sound.Init(ctx);
		if(!MainActivity.string2boolean(TheGoose.ca.getIniKey("AttackRandomly"))){
			int num = Arrays.asList(TheGoose.gooseTaskWeightedList).indexOf(TheGoose.GooseTask.CollectWindow_Meme);
			// 获取 `TheGoose.taskPickerDeck.indices` 中的索引
			int num2 = TheGoose.taskPickerDeck.indices[0];
			// 交换元素
			TheGoose.taskPickerDeck.indices[0] = TheGoose.taskPickerDeck.indices[num];
			TheGoose.taskPickerDeck.indices[num] = num2;
		}
		TheGoose.lFootPos = TheGoose.GetFootHome(false);
		TheGoose.rFootPos = TheGoose.GetFootHome(true);
        // 创建一个 2x2 像素的透明 Bitmap
        ShadowBitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        // 将 Bitmap 的像素设置为透明和深灰色
        ShadowBitmap.setPixel(0, 0, Color.TRANSPARENT);
        ShadowBitmap.setPixel(1, 1, Color.TRANSPARENT);
        ShadowBitmap.setPixel(1, 0, Color.TRANSPARENT);
        ShadowBitmap.setPixel(0, 1, 转换操作.转换颜色("#FFA9A9A9"));

        // 创建 Paint 对象，用于图像和线条的绘制
        ShadowPaint = new Paint();
        // 使用 Bitmap 创建阴影刷子（效果类似于 C# 中的 TextureBrush）
        ShadowPaint.setShader(new BitmapShader(ShadowBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        // 创建用于绘制阴影的 Paint 对象（类似于 C# 中的 Pen）
        TheGoose.ShadowPen = ShadowPaint;
        TheGoose.ShadowPen.setStrokeCap(Paint.Cap.ROUND); // 设置线条两端为圆形
        // 创建绘制的笔（类似于 C# 中的 Pen）
        DrawingPen = new Paint();
        DrawingPen.setColor(Color.WHITE); // 设置绘制颜色为白色
        DrawingPen.setStrokeCap(Paint.Cap.ROUND); // 设置线条两端为圆形
        DrawingPen.setAntiAlias(true); // 开启抗锯齿
        //drawingPen.setStyle(Paint.Style.STROKE); // 设置为只描边
        //DrawingPen.setStrokeWidth(5); // 设置线宽为 5
		TheGoose.SetTask(TheGoose.GooseTask.Wander);
    }
	
	// Token: 0x0600004A RID: 74 RVA: 0x000035BC File Offset: 0x000017BC
	public static void Tick()
	{
		//Cursor.Clip = Rectangle.Empty;
		/*
		if (TheGoose.currentTask != TheGoose.GooseTask.NabMouse && (Control.MouseButtons & MouseButtons.Left) == MouseButtons.Left && !TheGoose.lastFrameMouseButtonPressed && Vector2.Distance(TheGoose.position + new Vector2(0f, 14f), new Vector2((float)Cursor.Position.X, (float)Cursor.Position.Y)) < 30f)
		{
			TheGoose.SetTask(TheGoose.GooseTask.NabMouse);
		}
		*/
		//TheGoose.lastFrameMouseButtonPressed = ((Control.MouseButtons & MouseButtons.Left) == MouseButtons.Left);
		TheGoose.targetDirection = Vector2.Normalize(Vector2.subtract(TheGoose.targetPos,TheGoose.position));
		TheGoose.overrideExtendNeck = false;
		TheGoose.RunAI();
		Vector2 vector = Vector2.Lerp(Vector2.GetFromAngleDegrees(TheGoose.direction), TheGoose.targetDirection, 0.25f);
		TheGoose.direction = (float)Math.atan2((double)vector.y, (double)vector.x) * 57.2957764f;
		if (Vector2.Magnitude(TheGoose.velocity) > TheGoose.currentSpeed)
		{
			TheGoose.velocity = Vector2.multiply(Vector2.Normalize(TheGoose.velocity),TheGoose.currentSpeed);
		}
		TheGoose.velocity = Vector2.add(TheGoose.velocity,Vector2.multiply(Vector2.multiply(Vector2.Normalize(Vector2.subtract(TheGoose.targetPos,TheGoose.position)),TheGoose.currentAcceleration),0.008333334f));
		TheGoose.position = Vector2.add(TheGoose.position,Vector2.multiply(TheGoose.velocity,0.008333334f));
		TheGoose.SolveFeet();
		Vector2.Magnitude(TheGoose.velocity);
		int num = (TheGoose.overrideExtendNeck | TheGoose.currentSpeed >= 200f) ? 1 : 0;
		TheGoose.gooseRig.neckLerpPercent = SamMath.Lerp(TheGoose.gooseRig.neckLerpPercent, (float)num, 0.075f);
	}
	
	// Token: 0x06000054 RID: 84 RVA: 0x00004330 File Offset: 0x00002530
	private static void RunAI()
	{
		switch (TheGoose.currentTask)
		{
			case Wander:
				TheGoose.RunWander();
				return;
			case NabMouse:
				TheGoose.RunNabMouse();
				return;
			case CollectWindow_Meme:
			case CollectWindow_Notepad:
			case CollectWindow_Donate:
				break;
			case CollectWindow_DONOTSET:
				TheGoose.RunCollectWindow();
				return;
			case TrackMud:
				TheGoose.RunTrackMud();
				break;
			default:
				return;
		}
	}
	
	// Token: 0x04000037 RID: 55
	private static FootMark[] footMarks = new FootMark[64];
	
	// Token: 0x0600004E RID: 78 RVA: 0x00003BBC File Offset: 0x00001DBC
	private static void RunCollectWindow()
	{
		switch (TheGoose.taskCollectWindowInfo.stage)
		{
			case WalkingOffscreen:
				if (Vector2.Distance(TheGoose.position, TheGoose.targetPos) < 5f)
				{
					TheGoose.taskCollectWindowInfo.secsToWait = TheGoose.Task_CollectWindow.GetWaitTime();
					TheGoose.taskCollectWindowInfo.waitStartTime = Time.time;
					TheGoose.taskCollectWindowInfo.stage = TheGoose.Task_CollectWindow.Stage.WaitingToBringWindowBack;
					return;
				}
				break;
			case WaitingToBringWindowBack:
				if (Time.time - TheGoose.taskCollectWindowInfo.waitStartTime > TheGoose.taskCollectWindowInfo.secsToWait)
				{
					//TheGoose.taskCollectWindowInfo.mainForm.FormClosing += TheGoose.CollectMemeTask_CancelEarly;
					/*
					new Thread(delegate()
					{
						TheGoose.taskCollectWindowInfo.mainForm.ShowDialog();
					}).Start();
					*/
					switch (TheGoose.taskCollectWindowInfo.screenDirection)
					{
						case Left:
							TheGoose.targetPos.y = SamMath.Lerp(TheGoose.position.y, (float)(系统操作.取屏幕高度(ctx) / 2), SamMath.RandomRange(0.2f, 0.3f));
							//TheGoose.targetPos.x = (float)TheGoose.taskCollectWindowInfo.mainForm.Width + SamMath.RandomRange(15f, 20f);
							break;
						case Top:
							//TheGoose.targetPos.y = (float)TheGoose.taskCollectWindowInfo.mainForm.Height + SamMath.RandomRange(80f, 100f);
							TheGoose.targetPos.x = SamMath.Lerp(TheGoose.position.x, (float)(系统操作.取屏幕宽度(ctx) / 2), SamMath.RandomRange(0.2f, 0.3f));
							break;
						case Right:
							TheGoose.targetPos.y = SamMath.Lerp(TheGoose.position.y, (float)(系统操作.取屏幕高度(ctx) / 2), SamMath.RandomRange(0.2f, 0.3f));
							//TheGoose.targetPos.x = (float)系统操作.取屏幕宽度(ctx) - ((float)TheGoose.taskCollectWindowInfo.mainForm.Width + SamMath.RandomRange(20f, 30f));
							break;
					}
					//TheGoose.targetPos.x = SamMath.Clamp(TheGoose.targetPos.x, (float)(TheGoose.taskCollectWindowInfo.mainForm.Width + 55), (float)(系统操作.取屏幕宽度(ctx) - (TheGoose.taskCollectWindowInfo.mainForm.Width + 55)));
					//TheGoose.targetPos.y = SamMath.Clamp(TheGoose.targetPos.y, (float)(TheGoose.taskCollectWindowInfo.mainForm.Height + 80), (float)系统操作.取屏幕高度(ctx));
					TheGoose.taskCollectWindowInfo.stage = TheGoose.Task_CollectWindow.Stage.DraggingWindowBack;
					return;
				}
				break;
			case DraggingWindowBack:
				if (Vector2.Distance(TheGoose.position, TheGoose.targetPos) < 5f)
				{
					TheGoose.targetPos = Vector2.add(TheGoose.position,Vector2.multiply(Vector2.GetFromAngleDegrees(TheGoose.direction + 180f),40f));
					TheGoose.SetTask(TheGoose.GooseTask.Wander);
					return;
				}
				TheGoose.overrideExtendNeck = true;
				TheGoose.targetDirection = Vector2.subtract(TheGoose.position,TheGoose.targetPos);
				//TheGoose.taskCollectWindowInfo.mainForm.SetWindowPositionThreadsafe(TheGoose.ToIntPoint(TheGoose.gooseRig.head2EndPoint - TheGoose.taskCollectWindowInfo.windowOffsetToBeak));
				break;
			default:
				return;
		}
	}
	
	private static void RunNabMouse(){
		
	}
	
	// Token: 0x0200001B RID: 27
	private static class Task_CollectWindow
	{
	// Token: 0x06000089 RID: 137 RVA: 0x00002875 File Offset: 0x00000A75
	public static float GetWaitTime()
	{
		return SamMath.RandomRange(2f, 3.5f);
	}

	// Token: 0x040000AD RID: 173
	//public TheGoose.MovableForm mainForm;

	// Token: 0x040000AE RID: 174
	public TheGoose.Task_CollectWindow.Stage stage;

	// Token: 0x040000AF RID: 175
	public float secsToWait;

	// Token: 0x040000B0 RID: 176
	public float waitStartTime;

	// Token: 0x040000B1 RID: 177
	public TheGoose.Task_CollectWindow.ScreenDirection screenDirection;

	// Token: 0x040000B2 RID: 178
	public Vector2 windowOffsetToBeak;

	// Token: 0x02000026 RID: 38
	public enum Stage
	{
		// Token: 0x040000EC RID: 236
		WalkingOffscreen,
		// Token: 0x040000ED RID: 237
		WaitingToBringWindowBack,
		// Token: 0x040000EE RID: 238
		DraggingWindowBack
		}

	// Token: 0x02000027 RID: 39
	public enum ScreenDirection
	{
		// Token: 0x040000F0 RID: 240
		Left,
		// Token: 0x040000F1 RID: 241
		Top,
		// Token: 0x040000F2 RID: 242
		Right
		}
	}
	
	// Token: 0x06000055 RID: 85 RVA: 0x00004380 File Offset: 0x00002580
	private static TheGoose.Task_CollectWindow.ScreenDirection SetTargetOffscreen(boolean canExitTop)
	{
		int num = (int)TheGoose.position.x;
		TheGoose.Task_CollectWindow.ScreenDirection result = TheGoose.Task_CollectWindow.ScreenDirection.Left;
		TheGoose.targetPos = new Vector2(-50f, SamMath.Lerp(TheGoose.position.y, (float)(系统操作.取屏幕高度(ctx) / 2), 0.4f));
		if (num > 系统操作.取屏幕宽度(ctx) / 2)
		{
			num = 系统操作.取屏幕宽度(ctx) - (int)TheGoose.position.x;
			result = TheGoose.Task_CollectWindow.ScreenDirection.Right;
			TheGoose.targetPos = new Vector2((float)(系统操作.取屏幕宽度(ctx) + 50), SamMath.Lerp(TheGoose.position.y, (float)(系统操作.取屏幕高度(ctx) / 2), 0.4f));
		}
		if (canExitTop && (float)num > TheGoose.position.y)
		{
			result = TheGoose.Task_CollectWindow.ScreenDirection.Top;
			TheGoose.targetPos = new Vector2(SamMath.Lerp(TheGoose.position.x, (float)(系统操作.取屏幕宽度(ctx) / 2), 0.4f), -50f);
		}
		return result;
	}
	
	// Token: 0x06000050 RID: 80 RVA: 0x00003EFC File Offset: 0x000020FC
	private static void RunTrackMud()
	{
		switch (TheGoose.taskTrackMudInfo.stage)
		{
			case DecideToRun:
				TheGoose.SetTargetOffscreen(false);
				TheGoose.SetSpeed(TheGoose.SpeedTiers.Run);
				TheGoose.taskTrackMudInfo.stage = TheGoose.Task_TrackMud.Stage.RunningOffscreen;
				return;
			case RunningOffscreen:
				if (Vector2.Distance(TheGoose.position, TheGoose.targetPos) < 5f)
				{
					TheGoose.targetPos = new Vector2(SamMath.RandomRange(0f, (float)系统操作.取屏幕宽度(ctx)), SamMath.RandomRange(0f, (float)系统操作.取屏幕高度(ctx)));
					TheGoose.taskTrackMudInfo.nextDirChangeTime = Time.time + TheGoose.Task_TrackMud.GetDirChangeInterval();
					TheGoose.taskTrackMudInfo.timeToStopRunning = Time.time + 2f;
					TheGoose.trackMudEndTime = Time.time + 15f;
					TheGoose.taskTrackMudInfo.stage = TheGoose.Task_TrackMud.Stage.RunningWandering;
					Sound.PlayMudSquith();
					return;
				}
				break;
			case RunningWandering:
				if (Vector2.Distance(TheGoose.position, TheGoose.targetPos) < 5f || Time.time > TheGoose.taskTrackMudInfo.nextDirChangeTime)
				{
					TheGoose.targetPos = new Vector2(SamMath.RandomRange(0f, (float)系统操作.取屏幕宽度(ctx)), SamMath.RandomRange(0f, (float)系统操作.取屏幕高度(ctx)));
					TheGoose.taskTrackMudInfo.nextDirChangeTime = Time.time + TheGoose.Task_TrackMud.GetDirChangeInterval();
				}
				if (Time.time > TheGoose.taskTrackMudInfo.timeToStopRunning)
				{
					TheGoose.targetPos = Vector2.add(TheGoose.position,new Vector2(30f, 3f));
					TheGoose.targetPos.x = SamMath.Clamp(TheGoose.targetPos.x, 55f, (float)(系统操作.取屏幕宽度(ctx) - 55));
					TheGoose.targetPos.y = SamMath.Clamp(TheGoose.targetPos.y, 80f, (float)(系统操作.取屏幕高度(ctx) - 80));
					TheGoose.SetTask(TheGoose.GooseTask.Wander, false);
				}
				break;
			default:
				return;
		}
	}
	
	// Token: 0x06000056 RID: 86 RVA: 0x00004470 File Offset: 0x00002670
	private static void SolveFeet()
	{
		Vector2.GetFromAngleDegrees(TheGoose.direction);
		Vector2.GetFromAngleDegrees(TheGoose.direction + 90f);
		Vector2 footHome = TheGoose.GetFootHome(false);
		Vector2 footHome2 = TheGoose.GetFootHome(true);
		if (TheGoose.lFootMoveTimeStart < 0f && TheGoose.rFootMoveTimeStart < 0f)
		{
			if (Vector2.Distance(TheGoose.lFootPos, footHome) > 5f)
			{
				TheGoose.lFootMoveOrigin = TheGoose.lFootPos;
				TheGoose.lFootMoveDir = Vector2.Normalize(Vector2.subtract(footHome,TheGoose.lFootPos));
				TheGoose.lFootMoveTimeStart = Time.time;
				return;
			}
			if (Vector2.Distance(TheGoose.rFootPos, footHome2) > 5f)
			{
				TheGoose.rFootMoveOrigin = TheGoose.rFootPos;
				TheGoose.rFootMoveDir = Vector2.Normalize(Vector2.subtract(footHome2,TheGoose.rFootPos));
				TheGoose.rFootMoveTimeStart = Time.time;
				return;
			}
		}
		else if (TheGoose.lFootMoveTimeStart > 0f)
		{
			Vector2 b = Vector2.add(footHome,Vector2.multiply(Vector2.multiply(TheGoose.lFootMoveDir,0.4f),5f));
			if (Time.time <= TheGoose.lFootMoveTimeStart + TheGoose.stepTime)
			{
				float p = (Time.time - TheGoose.lFootMoveTimeStart) / TheGoose.stepTime;
				TheGoose.lFootPos = Vector2.Lerp(TheGoose.lFootMoveOrigin, b, Easings.CubicEaseInOut(p));
				return;
			}
			TheGoose.lFootPos = b;
			TheGoose.lFootMoveTimeStart = -1f;
			Sound.PlayPat();
			if (Time.time < TheGoose.trackMudEndTime)
			{
				TheGoose.AddFootMark(TheGoose.lFootPos);
				return;
			}
		}
		else if (TheGoose.rFootMoveTimeStart > 0f)
		{
			Vector2 b2 = Vector2.add(footHome2,Vector2.multiply(Vector2.multiply(TheGoose.rFootMoveDir,0.4f),5f));
			if (Time.time > TheGoose.rFootMoveTimeStart + TheGoose.stepTime)
			{
				TheGoose.rFootPos = b2;
				TheGoose.rFootMoveTimeStart = -1f;
				Sound.PlayPat();
				if (Time.time < TheGoose.trackMudEndTime)
				{
					TheGoose.AddFootMark(TheGoose.rFootPos);
					return;
				}
			}
			else
			{
				float p2 = (Time.time - TheGoose.rFootMoveTimeStart) / TheGoose.stepTime;
				TheGoose.rFootPos = Vector2.Lerp(TheGoose.rFootMoveOrigin, b2, Easings.CubicEaseInOut(p2));
			}
		}
	}

	// Token: 0x0600004B RID: 75 RVA: 0x00003780 File Offset: 0x00001980
	private static void RunWander()
	{
		if (Time.time - TheGoose.taskWanderInfo.wanderingStartTime > TheGoose.taskWanderInfo.wanderingDuration)
		{
			TheGoose.ChooseNextTask();
			return;
		}
		if (TheGoose.taskWanderInfo.pauseStartTime > 0f)
		{
			if (Time.time - TheGoose.taskWanderInfo.pauseStartTime > TheGoose.taskWanderInfo.pauseDuration)
			{
				TheGoose.taskWanderInfo.pauseStartTime = -1f;
				float num = TheGoose.Task_Wander.GetRandomWalkTime() * TheGoose.currentSpeed;
				TheGoose.targetPos = new Vector2(SamMath.RandomRange(0f, (float)系统操作.取屏幕宽度(ctx)), SamMath.RandomRange(0f, (float)系统操作.取屏幕高度(ctx)));
				if (Vector2.Distance(TheGoose.position, TheGoose.targetPos) > num)
				{
					TheGoose.targetPos = Vector2.add(TheGoose.position,Vector2.multiply(Vector2.Normalize(Vector2.subtract(TheGoose.targetPos,TheGoose.position)),num));
				}
				return;
			}
			TheGoose.velocity = Vector2.zero;
			return;
		}
		else
		{
			if (Vector2.Distance(TheGoose.position, TheGoose.targetPos) < 20f)
			{
				TheGoose.taskWanderInfo.pauseStartTime = Time.time;
				TheGoose.taskWanderInfo.pauseDuration = TheGoose.Task_Wander.GetRandomPauseDuration();
				return;
			}
			return;
		}
	}
	
	// Token: 0x06000057 RID: 87 RVA: 0x00004670 File Offset: 0x00002870
	private static Vector2 GetFootHome(boolean rightFoot)
	{
		float b = (float)(rightFoot ? 1 : 0);
		Vector2 a = Vector2.multiply(Vector2.GetFromAngleDegrees(TheGoose.direction + 90f),b);
		return Vector2.add(TheGoose.position,Vector2.multiply(a,6f));
	}

	// Token: 0x06000058 RID: 88 RVA: 0x000046B4 File Offset: 0x000028B4
	private static void AddFootMark(Vector2 markPos)
	{
		TheGoose.footMarks[TheGoose.footMarkIndex].time = Time.time;
		TheGoose.footMarks[TheGoose.footMarkIndex].position = markPos;
		TheGoose.footMarkIndex++;
		if (TheGoose.footMarkIndex >= TheGoose.footMarks.length)
		{
			TheGoose.footMarkIndex = 0;
		}
	}
	
	private static void SetSpeed(TheGoose.SpeedTiers tier)
	{
		switch (tier)
		{
			case Walk:
				TheGoose.currentSpeed = 80f;
				TheGoose.currentAcceleration = 1300f;
				TheGoose.stepTime = 0.2f;
				break;
			case Run:
				TheGoose.currentSpeed = 200f;
				TheGoose.currentAcceleration = 1300f;
				TheGoose.stepTime = 0.2f;
				break;
			case Charge:
				TheGoose.currentSpeed = 400f;
				TheGoose.currentAcceleration = 2300f;
				TheGoose.stepTime = 0.1f;
				break;
			default:
				break;
		}
	}
	
	// Token: 0x06000051 RID: 81 RVA: 0x000040D8 File Offset: 0x000022D8
	private static void ChooseNextTask()
	{
		if (!MainActivity.string2boolean(TheGoose.ca.getIniKey("AttackRandomly")) && Time.time < Float.parseFloat(TheGoose.ca.getIniKey("FirstWanderTimeSeconds")) + 1f)
		{
			TheGoose.SetTask(TheGoose.GooseTask.TrackMud);
			return;
		}
		if (Time.time > 480f && !TheGoose.hasAskedForDonation)
		{
			TheGoose.hasAskedForDonation = true;
			TheGoose.SetTask(TheGoose.GooseTask.CollectWindow_Donate);
			return;
		}
		TheGoose.GooseTask gooseTask = TheGoose.gooseTaskWeightedList[TheGoose.taskPickerDeck.Next()];
		while (!MainActivity.string2boolean(TheGoose.ca.getIniKey("AttackRandomly")))
		{
			if (gooseTask != TheGoose.GooseTask.NabMouse)
			{
				break;
			}
			gooseTask = TheGoose.gooseTaskWeightedList[TheGoose.taskPickerDeck.Next()];
		}
		TheGoose.SetTask(gooseTask);
	}

	// Token: 0x06000052 RID: 82 RVA: 0x00002532 File Offset: 0x00000732
	private static void SetTask(TheGoose.GooseTask task)
	{
		TheGoose.SetTask(task, true);
	}

	// Token: 0x06000053 RID: 83 RVA: 0x0000416C File Offset: 0x0000236C
	private static void SetTask(TheGoose.GooseTask task, boolean honck)
	{
		if (honck)
		{
			Sound.HONCC();
		}
		TheGoose.currentTask = task;
		switch (task)
		{
			case Wander:
				TheGoose.SetSpeed(TheGoose.SpeedTiers.Walk);
				TheGoose.taskWanderInfo = new Task_Wander();
				TheGoose.taskWanderInfo.pauseStartTime = -1f;
				TheGoose.taskWanderInfo.wanderingStartTime = Time.time;
				TheGoose.taskWanderInfo.wanderingDuration = TheGoose.Task_Wander.GetRandomWanderDuration();
				break;
			case NabMouse:
				TheGoose.taskNabMouseInfo = new TheGoose.Task_NabMouse();
				TheGoose.taskNabMouseInfo.chaseStartTime = Time.time;
				break;
			case CollectWindow_Meme:
				TheGoose.taskCollectWindowInfo = new TheGoose.Task_CollectWindow();
				//TheGoose.taskCollectWindowInfo.mainForm = new TheGoose.SimpleImageForm();
				TheGoose.SetTask(TheGoose.GooseTask.CollectWindow_DONOTSET, false);
				break;
			case CollectWindow_Notepad:
				TheGoose.taskCollectWindowInfo = new TheGoose.Task_CollectWindow();
				//TheGoose.taskCollectWindowInfo.mainForm = new TheGoose.SimpleTextForm();
				TheGoose.SetTask(TheGoose.GooseTask.CollectWindow_DONOTSET, false);
				break;
			case CollectWindow_Donate:
				TheGoose.taskCollectWindowInfo = new TheGoose.Task_CollectWindow();
				//TheGoose.taskCollectWindowInfo.mainForm = new TheGoose.SimpleDonateForm();
				TheGoose.SetTask(TheGoose.GooseTask.CollectWindow_DONOTSET, false);
				break;
			case CollectWindow_DONOTSET:
				TheGoose.taskCollectWindowInfo.screenDirection = TheGoose.SetTargetOffscreen(false);
				switch (TheGoose.taskCollectWindowInfo.screenDirection)
				{
					case Left:
						//TheGoose.taskCollectWindowInfo.windowOffsetToBeak = new Vector2((float)TheGoose.taskCollectWindowInfo.mainForm.Width, (float)(TheGoose.taskCollectWindowInfo.mainForm.Height / 2));
						break;
					case Top:
						//TheGoose.taskCollectWindowInfo.windowOffsetToBeak = new Vector2((float)(TheGoose.taskCollectWindowInfo.mainForm.Width / 2), (float)TheGoose.taskCollectWindowInfo.mainForm.Height);
						break;
					case Right:
						//TheGoose.taskCollectWindowInfo.windowOffsetToBeak = new Vector2(0f, (float)(TheGoose.taskCollectWindowInfo.mainForm.Height / 2));
						break;
					default:
						break;
				}
				break;
			case TrackMud:
				TheGoose.taskTrackMudInfo = new TheGoose.Task_TrackMud();
				break;
			default:
				break;
		}
	}
	
	private static Vector2 position = new Vector2(300f, 300f);

	// Token: 0x0400001D RID: 29
	private static Vector2 velocity = new Vector2(0f, 0f);

	// Token: 0x0400001E RID: 30
	private static float direction = 90f;

	// Token: 0x0400001F RID: 31
	private static Vector2 targetDirection;

	// Token: 0x04000020 RID: 32
	private static boolean overrideExtendNeck;

	// Token: 0x04000021 RID: 33
	private final TheGoose.GooseTask FirstUX_FirstTask = TheGoose.GooseTask.TrackMud;

	// Token: 0x04000022 RID: 34
	private final TheGoose.GooseTask FirstUX_SecondTask = TheGoose.GooseTask.CollectWindow_Meme;

	// Token: 0x04000023 RID: 35
	private static Vector2 targetPos = new Vector2(300f, 300f);

	// Token: 0x04000024 RID: 36
	private static float targetDir = 90f;

	// Token: 0x04000025 RID: 37
	private static float currentSpeed = 80f;

	// Token: 0x04000026 RID: 38
	private static float currentAcceleration = 1300f;

	// Token: 0x04000027 RID: 39
	private static float stepTime = 0.2f;

	// Token: 0x04000028 RID: 40
	private final float WalkSpeed = 80f;

	// Token: 0x04000029 RID: 41
	private final float RunSpeed = 200f;

	// Token: 0x0400002A RID: 42
	private final float ChargeSpeed = 400f;

	// Token: 0x0400002B RID: 43
	private final float turnSpeed = 120f;

	// Token: 0x0400002C RID: 44
	private final float AccelerationNormal = 1300f;

	// Token: 0x0400002D RID: 45
	private final float AccelerationCharged = 2300f;

	// Token: 0x0400002E RID: 46
	private final float StopRadius = -10f;

	// Token: 0x0400002F RID: 47
	private final float StepTimeNormal = 0.2f;

	// Token: 0x04000030 RID: 48
	private final float StepTimeCharged = 0.1f;

	// Token: 0x04000031 RID: 49
	private static float trackMudEndTime = -1f;

	// Token: 0x04000032 RID: 50
	private final float DurationToTrackMud = 15f;

	// Token: 0x04000038 RID: 56
	private static int footMarkIndex = 0;

	// Token: 0x04000039 RID: 57
	private static boolean lastFrameMouseButtonPressed = false;

	// Token: 0x0400003A RID: 58
	private static TheGoose.GooseTask currentTask;

	// Token: 0x0400003B RID: 59
	private static TheGoose.Task_Wander taskWanderInfo;

	// Token: 0x0400003C RID: 60
	private static TheGoose.Task_NabMouse taskNabMouseInfo;

	// Token: 0x0400003D RID: 61
	//private static Rectangle tmpRect = new Rectangle();

	// Token: 0x0400003E RID: 62
	//private static Size tmpSize = new Size();

	// Token: 0x0400003F RID: 63
	private static boolean hasAskedForDonation = false;

	// Token: 0x04000040 RID: 64
	private static TheGoose.Task_CollectWindow taskCollectWindowInfo;

	// Token: 0x04000041 RID: 65
	private static TheGoose.Task_TrackMud taskTrackMudInfo;

	// Token: 0x04000042 RID: 66
	private static TheGoose.GooseTask[] gooseTaskWeightedList = new TheGoose.GooseTask[]
	{
		TheGoose.GooseTask.TrackMud,
		TheGoose.GooseTask.TrackMud,
		TheGoose.GooseTask.CollectWindow_Meme,
		TheGoose.GooseTask.CollectWindow_Meme,
		TheGoose.GooseTask.CollectWindow_Notepad,
		TheGoose.GooseTask.NabMouse,
		TheGoose.GooseTask.NabMouse,
		TheGoose.GooseTask.NabMouse
	};

	// Token: 0x04000043 RID: 67
	private static Deck taskPickerDeck = new Deck(TheGoose.gooseTaskWeightedList.length);

	// Token: 0x04000044 RID: 68
	private static Vector2 lFootPos;

	// Token: 0x04000045 RID: 69
	private static Vector2 rFootPos;

	// Token: 0x04000046 RID: 70
	private static float lFootMoveTimeStart = -1f;

	// Token: 0x04000047 RID: 71
	private static float rFootMoveTimeStart = -1f;

	// Token: 0x04000048 RID: 72
	private static Vector2 lFootMoveOrigin;

	// Token: 0x04000049 RID: 73
	private static Vector2 rFootMoveOrigin;

	// Token: 0x0400004A RID: 74
	private static Vector2 lFootMoveDir;

	// Token: 0x0400004B RID: 75
	private static Vector2 rFootMoveDir;

	// Token: 0x0400004C RID: 76
	private final float wantStepAtDistance = 5f;

	// Token: 0x0400004D RID: 77
	private final int feetDistanceApart = 6;

	// Token: 0x0400004E RID: 78
	private final float overshootFraction = 0.4f;
	
	private static TheGoose.Rig gooseRig = new Rig();

	// Token: 0x02000017 RID: 23
	private enum SpeedTiers
	{
		// Token: 0x0400008E RID: 142
		Walk,
		// Token: 0x0400008F RID: 143
		Run,
		// Token: 0x04000090 RID: 144
		Charge;
		}

	// Token: 0x02000018 RID: 24
	private enum GooseTask
	{
		// Token: 0x04000092 RID: 146
		Wander,
		// Token: 0x04000093 RID: 147
		NabMouse,
		// Token: 0x04000094 RID: 148
		CollectWindow_Meme,
		// Token: 0x04000095 RID: 149
		CollectWindow_Notepad,
		// Token: 0x04000096 RID: 150
		CollectWindow_Donate,
		// Token: 0x04000097 RID: 151
		CollectWindow_DONOTSET,
		// Token: 0x04000098 RID: 152
		TrackMud,
		// Token: 0x04000099 RID: 153
		Count;
		}

	// Token: 0x02000019 RID: 25
	private static class Task_Wander
	{
	// Token: 0x06000085 RID: 133 RVA: 0x00002803 File Offset: 0x00000A03
	public static float GetRandomPauseDuration()
	{
		return 1f + (float)SamMath.Rand.nextDouble() * 1f;
	}

	// Token: 0x06000086 RID: 134 RVA: 0x0000281C File Offset: 0x00000A1C
	public static float GetRandomWanderDuration()
	{
		if (Time.time < 1f)
		{
			return Float.parseFloat(ca.getIniKey("FirstWanderTimeSeconds"));
		}
		return SamMath.RandomRange(Float.parseFloat(ca.getIniKey("MinWanderingTimeSeconds")), Float.parseFloat(ca.getIniKey("MaxWanderingTimeSeconds")));
	}

	// Token: 0x06000087 RID: 135 RVA: 0x0000284E File Offset: 0x00000A4E
	public static float GetRandomWalkTime()
	{
		return SamMath.RandomRange(1f, 6f);
	}

	// Token: 0x0400009A RID: 154
	private final float MinPauseTime = 1f;

	// Token: 0x0400009B RID: 155
	private final float MaxPauseTime = 2f;

	// Token: 0x0400009C RID: 156
	public final float GoodEnoughDistance = 20f;

	// Token: 0x0400009D RID: 157
	public float wanderingStartTime;

	// Token: 0x0400009E RID: 158
	public float wanderingDuration;

	// Token: 0x0400009F RID: 159
	public float pauseStartTime;

	// Token: 0x040000A0 RID: 160
	public float pauseDuration;
	}
	
	// Token: 0x0200001A RID: 26
	private static class Task_NabMouse
	{
	// Token: 0x040000A1 RID: 161
	public TheGoose.Task_NabMouse.Stage currentStage;

	// Token: 0x040000A2 RID: 162
	public Vector2 dragToPoint;

	// Token: 0x040000A3 RID: 163
	public float grabbedOriginalTime;

	// Token: 0x040000A4 RID: 164
	public float chaseStartTime;

	// Token: 0x040000A5 RID: 165
	public Vector2 originalVectorToMouse;

	// Token: 0x040000A6 RID: 166
	public final float MouseGrabDistance = 15f;

	// Token: 0x040000A7 RID: 167
	public final float MouseSuccTime = 0.06f;

	// Token: 0x040000A8 RID: 168
	public final float MouseDropDistance = 30f;

	// Token: 0x040000A9 RID: 169
	public final float MinRunTime = 2f;

	// Token: 0x040000AA RID: 170
	public final float MaxRunTime = 4f;

	// Token: 0x040000AB RID: 171
	public final float GiveUpTime = 9f;

	// Token: 0x040000AC RID: 172
	public static Vector2 StruggleRange = new Vector2(3f, 3f);

	// Token: 0x02000025 RID: 37
	public enum Stage
	{
		// Token: 0x040000E8 RID: 232
		SeekingMouse,
		// Token: 0x040000E9 RID: 233
		DraggingMouseAway,
		// Token: 0x040000EA RID: 234
		Decelerating
		}
	}
	// Token: 0x02000020 RID: 32
	private static class Task_TrackMud
	{
	// Token: 0x06000099 RID: 153 RVA: 0x000028D3 File Offset: 0x00000AD3
	public static float GetDirChangeInterval()
	{
		return 100f;
	}

	// Token: 0x040000C0 RID: 192
	public final float DurationToRunAmok = 2f;

	// Token: 0x040000C1 RID: 193
	public float nextDirChangeTime;

	// Token: 0x040000C2 RID: 194
	public float timeToStopRunning;

	// Token: 0x040000C3 RID: 195
	public TheGoose.Task_TrackMud.Stage stage;

	// Token: 0x0200002A RID: 42
	public enum Stage
	{
		// Token: 0x040000F8 RID: 248
		DecideToRun,
		// Token: 0x040000F9 RID: 249
		RunningOffscreen,
		// Token: 0x040000FA RID: 250
		RunningWandering
		}
	}

	// Token: 0x02000021 RID: 33
	private static class Rig
	{
	// Token: 0x040000C4 RID: 196
	public final int UnderBodyRadius = 15;

	// Token: 0x040000C5 RID: 197
	public final int UnderBodyLength = 7;

	// Token: 0x040000C6 RID: 198
	public final int UnderBodyElevation = 9;

	// Token: 0x040000C7 RID: 199
	public Vector2 underbodyCenter;

	// Token: 0x040000C8 RID: 200
	public final int BodyRadius = 22;

	// Token: 0x040000C9 RID: 201
	public final int BodyLength = 11;

	// Token: 0x040000CA RID: 202
	public final int BodyElevation = 14;

	// Token: 0x040000CB RID: 203
	public Vector2 bodyCenter;

	// Token: 0x040000CC RID: 204
	public final int NeccRadius = 13;

	// Token: 0x040000CD RID: 205
	public final int NeccHeight1 = 20;

	// Token: 0x040000CE RID: 206
	public final int NeccExtendForward1 = 3;

	// Token: 0x040000CF RID: 207
	public final int NeccHeight2 = 10;

	// Token: 0x040000D0 RID: 208
	public final int NeccExtendForward2 = 16;

	// Token: 0x040000D1 RID: 209
	public float neckLerpPercent;

	// Token: 0x040000D2 RID: 210
	public Vector2 neckCenter;

	// Token: 0x040000D3 RID: 211
	public Vector2 neckBase;

	// Token: 0x040000D4 RID: 212
	public Vector2 neckHeadPoint;

	// Token: 0x040000D5 RID: 213
	public final int HeadRadius1 = 15;

	// Token: 0x040000D6 RID: 214
	public final int HeadLength1 = 3;

	// Token: 0x040000D7 RID: 215
	public final int HeadRadius2 = 10;

	// Token: 0x040000D8 RID: 216
	public final int HeadLength2 = 5;

	// Token: 0x040000D9 RID: 217
	public Vector2 head1EndPoint;

	// Token: 0x040000DA RID: 218
	public Vector2 head2EndPoint;

	// Token: 0x040000DB RID: 219
	public final int EyeRadius = 2;

	// Token: 0x040000DC RID: 220
	public final int EyeElevation = 3;

	// Token: 0x040000DD RID: 221
	public final float IPD = 5f;

	// Token: 0x040000DE RID: 222
	public final float EyesForward = 5f;
	}
	
	// Token: 0x06000059 RID: 89 RVA: 0x00004710 File Offset: 0x00002910
	public static void UpdateRig()
	{
		float num = TheGoose.direction;
		int num2 = (int)TheGoose.position.x;
		int num3 = (int)TheGoose.position.y;
		Vector2 a = new Vector2((float)num2, (float)num3);
		Vector2 b = new Vector2(1.3f, 0.4f);
		Vector2 fromAngleDegrees = Vector2.GetFromAngleDegrees(num);
		fromAngleDegrees = Vector2.multiply(fromAngleDegrees,b);
		b = Vector2.multiply(Vector2.GetFromAngleDegrees(num + 90f),b);
		Vector2 a2 = new Vector2(0f, -1f);
		TheGoose.gooseRig.underbodyCenter = Vector2.add(a,Vector2.multiply(a2,9f));
		TheGoose.gooseRig.bodyCenter = Vector2.add(a,Vector2.multiply(a2,14f));
		int num4 = (int)SamMath.Lerp(20f, 10f, TheGoose.gooseRig.neckLerpPercent);
		int num5 = (int)SamMath.Lerp(3f, 16f, TheGoose.gooseRig.neckLerpPercent);
		TheGoose.gooseRig.neckCenter = Vector2.add(a,Vector2.multiply(a2,(float)(14 + num4)));
		TheGoose.gooseRig.neckBase = Vector2.add(TheGoose.gooseRig.bodyCenter,Vector2.multiply(fromAngleDegrees,15f));
		TheGoose.gooseRig.neckHeadPoint = Vector2.add(Vector2.add(TheGoose.gooseRig.neckBase,Vector2.multiply(fromAngleDegrees,(float)num5)),Vector2.multiply(a2,(float)num4));
		TheGoose.gooseRig.head1EndPoint = Vector2.subtract(Vector2.add(TheGoose.gooseRig.neckHeadPoint,Vector2.multiply(fromAngleDegrees,3f)),Vector2.multiply(a2,1f));
		TheGoose.gooseRig.head2EndPoint = Vector2.add(TheGoose.gooseRig.head1EndPoint,Vector2.multiply(fromAngleDegrees,5f));
	}
	
	public static void Render()
	{
		Canvas g = canvas;
		for (int i = 0; i < TheGoose.footMarks.length; i++){
			if (TheGoose.footMarks[i] != null && TheGoose.footMarks[i].time != 0.0f){
				float num = TheGoose.footMarks[i].time + 8.5f;
				float p = SamMath.Clamp(Time.time - num, 0f, 1f) / 1f;
				float num2 = SamMath.Lerp(3f, 0f, p);
				Paint p3 = new Paint();
				p3.setColor(转换操作.转换颜色("#FF8B4513"));
				TheGoose.FillCircleFromCenter(g, p3, TheGoose.footMarks[i].position, (int)num2);
			}
		}
		TheGoose.UpdateRig();
		float num3 = TheGoose.direction;
		int num4 = (int)TheGoose.position.x;
		int num5 = (int)TheGoose.position.y;
		Vector2 vector = new Vector2((float)num4, (float)num5);
		Vector2 b = new Vector2(1.3f, 0.4f);
		Vector2 fromAngleDegrees = Vector2.GetFromAngleDegrees(num3);
		fromAngleDegrees = Vector2.multiply(fromAngleDegrees,b);
		Vector2 fromAngleDegrees2 = Vector2.GetFromAngleDegrees(num3 + 90f);
		fromAngleDegrees2 = Vector2.multiply(fromAngleDegrees2,b);
		Vector2 a = new Vector2(0f, -1f);
		DrawingPen.setColor(Color.WHITE);
		Paint p1 = new Paint();
		p1.setColor(转换操作.转换颜色("#FFFFA500"));
		TheGoose.FillCircleFromCenter(g, p1, TheGoose.lFootPos, 4);
		TheGoose.FillCircleFromCenter(g, p1, TheGoose.rFootPos, 4);
		TheGoose.FillEllipseFromCenter(g, TheGoose.ShadowPen, (int)vector.x, (int)vector.y, 20, 15);
		DrawingPen.setColor(转换操作.转换颜色("#FFD3D3D3"));//Color.LightGray
		DrawingPen.setStrokeWidth(24f);
		DrawLine(TheGoose.DrawingPen, Vector2.add(TheGoose.gooseRig.bodyCenter,Vector2.multiply(fromAngleDegrees,11f)), Vector2.subtract(TheGoose.gooseRig.bodyCenter,Vector2.multiply(fromAngleDegrees,11f)));
		DrawingPen.setStrokeWidth(15f);
		DrawLine(TheGoose.DrawingPen, TheGoose.gooseRig.neckBase, TheGoose.gooseRig.neckHeadPoint);
		DrawingPen.setStrokeWidth(17f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(TheGoose.gooseRig.neckHeadPoint), TheGoose.ToIntPoint(TheGoose.gooseRig.head1EndPoint));
		DrawingPen.setStrokeWidth(12f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(TheGoose.gooseRig.head1EndPoint), TheGoose.ToIntPoint(TheGoose.gooseRig.head2EndPoint));
		DrawingPen.setColor(转换操作.转换颜色("#FFD3D3D3"));//Color.LightGray
		DrawingPen.setStrokeWidth(15f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(Vector2.add(TheGoose.gooseRig.underbodyCenter,Vector2.multiply(fromAngleDegrees,7f))), TheGoose.ToIntPoint(Vector2.subtract(TheGoose.gooseRig.underbodyCenter,Vector2.multiply(fromAngleDegrees,7f))));
		DrawingPen.setColor(Color.WHITE);
		DrawingPen.setStrokeWidth(22f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(Vector2.add(TheGoose.gooseRig.bodyCenter,Vector2.multiply(fromAngleDegrees,11f))), TheGoose.ToIntPoint(Vector2.subtract(TheGoose.gooseRig.bodyCenter,Vector2.multiply(fromAngleDegrees,11f))));
		DrawingPen.setStrokeWidth(13f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(TheGoose.gooseRig.neckBase), TheGoose.ToIntPoint(TheGoose.gooseRig.neckHeadPoint));
		DrawingPen.setStrokeWidth(15f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(TheGoose.gooseRig.neckHeadPoint), TheGoose.ToIntPoint(TheGoose.gooseRig.head1EndPoint));
		DrawingPen.setStrokeWidth(10f);
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(TheGoose.gooseRig.head1EndPoint), TheGoose.ToIntPoint(TheGoose.gooseRig.head2EndPoint));
		DrawingPen.setStrokeWidth(9f);
		TheGoose.DrawingPen.setColor(转换操作.转换颜色("#FFFFA500"));
		Vector2 vector2 = Vector2.add(TheGoose.gooseRig.head2EndPoint,Vector2.multiply(fromAngleDegrees,3f));
		DrawLine(TheGoose.DrawingPen, TheGoose.ToIntPoint(TheGoose.gooseRig.head2EndPoint), TheGoose.ToIntPoint(vector2));
		Vector2 pos = Vector2.add(Vector2.add(Vector2.add(TheGoose.gooseRig.neckHeadPoint,Vector2.multiply(a,3f)),Vector2.multiply(Vector2.multiply(Vector2.negate(fromAngleDegrees2),b),5f)),Vector2.multiply(fromAngleDegrees,5f));
		Vector2 pos2 = Vector2.add(Vector2.add(Vector2.add(TheGoose.gooseRig.neckHeadPoint,Vector2.multiply(a,3f)),Vector2.multiply(Vector2.multiply(fromAngleDegrees2,b),5f)),Vector2.multiply(fromAngleDegrees,5f));
		Paint p2 = new Paint();
		p2.setColor(转换操作.转换颜色("#FF000000"));
		TheGoose.FillCircleFromCenter(g, p2, pos, 2);
		TheGoose.FillCircleFromCenter(g, p2, pos2, 2);
	}
	// Token: 0x0600005D RID: 93 RVA: 0x00002563 File Offset: 0x00000763
	public static void FillEllipseFromCenter(Canvas g, Paint p, int x, int y, int xRadius, int yRadius)
	{
		g.drawOval(new RectF(x - xRadius, y - yRadius, x + xRadius, y + yRadius),p);
	}
	// Token: 0x0600005B RID: 91 RVA: 0x0000253B File Offset: 0x0000073B
	public static void FillCircleFromCenter(Canvas g, Paint brush, Vector2 pos, int radius)
	{
		TheGoose.FillEllipseFromCenter(g, brush, (int)pos.x, (int)pos.y, radius, radius);
	}
	private static void DrawLine(Paint p,Vector2 startVec,Vector2 endVec){
		canvas.drawLine(startVec.x,startVec.y,endVec.x,endVec.y,p);
	}
	private static Vector2 ToIntPoint(Vector2 vec){
		return vec;
	}
}

