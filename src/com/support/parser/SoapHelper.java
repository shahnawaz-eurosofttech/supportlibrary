package com.support.parser;

import java.io.IOException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class SoapHelper {
	public static class Builder {
		private static final String mURL1 = "http://favouritehatfield.co.uk/Service1.asmx?";
		private static final String mURL2 = "http://favouritehatfield.co.uk/Service2.asmx?";

		private String mURL = "http://favouritehatfield.co.uk/Service2.asmx?";
		private String mMethod = "GetServerVehicles";
		private String mNameSpace = "http://tempuri.org/";
		private String mSoapAction = "http://tempuri.org/GetServerVehicles";

		private SoapSerializationEnvelope mEnvelope;

		private ArrayList<PropertyInfo> mInfoArray = new ArrayList<PropertyInfo>();

		public Builder(int service) {
			switch (service) {
			case 1:
				mURL = mURL1;
				break;
			case 2:
				mURL = mURL2;
				break;
			default:
				break;
			}

			mEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
			mEnvelope.dotNet = true;

		}

		public Builder setMethodName(String method, boolean concatinate) {
			if (concatinate) {
				mMethod = method;
				mSoapAction = mNameSpace + method;
			} else
				mMethod = method;

			return this;
		}

		public Builder setMethodName(String method) {
			mMethod = method;
			return this;
		}

		public Builder setSoapAction(String action) {
			mSoapAction = action;
			return this;
		}

		public Builder addProperty(String name, Object value, Object clazz) {
			PropertyInfo info = new PropertyInfo();
			info.setName(name);
			info.setType(clazz);
			info.setValue(value);

			mInfoArray.add(info);

			return this;
		}

		public SoapSerializationEnvelope executeEnvelope() throws IOException, XmlPullParserException {
			HttpTransportSE transport = new HttpTransportSE(mURL);
			SoapObject soapObject = new SoapObject(mNameSpace, mMethod);

			for (PropertyInfo info : mInfoArray)
				soapObject.addProperty(info);

			mEnvelope.setOutputSoapObject(soapObject);

			transport.call(mSoapAction, mEnvelope);
			return mEnvelope;
		}

		public String getResponse() throws SoapFault, IOException, XmlPullParserException {
			SoapPrimitive response = (SoapPrimitive) executeEnvelope().getResponse();
			return response.toString();
		}

	}

}
