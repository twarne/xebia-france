package fr.xebia.aop.aspects;

public aspect MonitorManager percflow(onManager()){

	pointcut onManager() : execution(* fr.xebia.aop.app.CaddyManager.purchase(..));
	pointcut onDao() : execution(* fr.xebia.aop.app.CaddyDAO.persist(..));
	
	int managerCalls = 0;
	int daoCalls = 0;

	Object around() : onManager() {
		managerCalls++;
		try {
			return proceed();
		} finally {
			System.out
					.println(thisJoinPointStaticPart.getSignature()
							+ " managerCalls/daoCalls " + managerCalls + "/"
							+ daoCalls);
		}
	}

	before() : onDao() {
		daoCalls++;
	}

}
