/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid.SamEngine;

public class Deck{
	public Deck(int Length)
	{
		this.indices = new int[Length];
		this.Reshuffle();
	}

	// Token: 0x06000028 RID: 40 RVA: 0x00002E90 File Offset: 0x00001090
	public void Reshuffle()
	{
		for (int i = 0; i < this.indices.length; i++)
		{
			this.indices[i] = i;
			int num = (int)SamMath.RandomRange(0f, (float)i);
			int num2 = this.indices[i];
			this.indices[i] = this.indices[num];
			this.indices[num] = num2;
		}
	}

	// Token: 0x06000029 RID: 41 RVA: 0x000022C9 File Offset: 0x000004C9
	public int Next()
	{
		int result = this.indices[this.i];
		this.i++;
		if (this.i >= this.indices.length)
		{
			this.Reshuffle();
			this.i = 0;
		}
		return result;
	}

	// Token: 0x0400000A RID: 10
	public int[] indices;

	// Token: 0x0400000B RID: 11
	private int i;
}
