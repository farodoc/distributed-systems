package sr.thrift.server;

import org.apache.thrift.TException;
import sr.gen.thrift.Calculator;
import sr.gen.thrift.InvalidArguments;

import java.util.List;

public class CalculatorHandler implements Calculator.Iface {

	int id;

	public CalculatorHandler(int id) {
		this.id = id;
	}

	@Override
	public int add(int n1, int n2) {
		System.out.println("CalcHandler#" + id + " add(" + n1 + "," + n2 + ")");
		if(n1 > 1000 || n2 > 1000) { 
			try { Thread.sleep(6000); } catch(java.lang.InterruptedException ex) { }
			System.out.println("DONE");
		}
		return n1 + n2;
	}

	@Override
	public int subtract(int num1, int num2) throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double median(List<Integer> numbers) throws TException {
		System.out.println("CalcHandler#" + id + " median(" + numbers.toString() + ")");

		if (numbers.size() == 0) {
			throw new InvalidArguments(0, "no data");
		}

		List<Integer> sorted = numbers.stream().sorted().toList();
		int middle = (sorted.size() - 1) / 2;

		double result = sorted.size() % 2 == 0 ? (double) (sorted.get(middle) + sorted.get(middle + 1)) / 2 : sorted.get(middle);

		System.out.println("DONE");
		return result;
	}

}

