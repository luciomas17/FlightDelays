package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();

		model.createGraph(100);
		
		if(model.testConnection(11, 297))
			System.out.println("Connessi");
		else
			System.out.println("Non connessi");
		
		System.out.println(model.findRoute(11, 297));
	}

}
