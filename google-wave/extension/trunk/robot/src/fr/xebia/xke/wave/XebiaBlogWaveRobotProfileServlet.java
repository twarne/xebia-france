package fr.xebia.xke.wave;

import com.google.wave.api.ProfileServlet;

public class XebiaBlogWaveRobotProfileServlet extends ProfileServlet {
	@Override
	public String getRobotName() {
		return "XebiaBlog";
	}

	@Override
	public String getRobotAvatarUrl() {
		return "http://blog.xebia.fr/favicon.png";
	}

	@Override
	public String getRobotProfilePageUrl() {
		return "http://blog.xebia.fr/";
	}
}
