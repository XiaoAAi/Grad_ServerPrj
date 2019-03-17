package code.server;

public class MyCRC16 {
	
	public static int ModBusCRC16(byte[] pData, int siLen)
	{
		if (null == pData || siLen <= 0)
		{
			return 0;
		}
		
		int u16CRC = 0xFFFF;
		int i = 0;
		int j = 0;
		for (i = 0; i < siLen; i++)
		{
			u16CRC ^= byteToInteger(pData[i]);
			for(j = 0; j <= 7; j++)
			{
				if ((u16CRC & 0x0001) == 1)
				{
					u16CRC = (u16CRC >> 1) ^ 0xA001;
				}
				else
				{
					u16CRC = u16CRC >> 1;  
				}
			}
		}
	 
		int siRet = 0;
		siRet = (u16CRC & 0x00FF) << 8; 
		siRet |= u16CRC >> 8;
		
		return siRet;
	}

	public static int byteToInteger(byte b) {
		int value;
		value = b & 0xff;
		return value;
	}	
	
	
}




