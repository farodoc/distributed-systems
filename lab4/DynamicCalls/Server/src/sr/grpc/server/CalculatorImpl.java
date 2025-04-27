package sr.grpc.server;

import io.grpc.stub.StreamObserver;
import sr.grpc.gen.*;
import sr.grpc.gen.CalculatorGrpc.CalculatorImplBase;

public class CalculatorImpl extends CalculatorImplBase 
{
	@Override
	public void add(ArithmeticOpArguments request,
			StreamObserver<ArithmeticOpResult> responseObserver)
	{
		System.out.println("addRequest (" + request.getArg1() + ", " + request.getArg2() +")");
		int val = request.getArg1() + request.getArg2();
		ArithmeticOpResult result = ArithmeticOpResult.newBuilder().setRes(val).build();
		if(request.getArg1() > 100 && request.getArg2() > 100) try { Thread.sleep(5000); } catch(java.lang.InterruptedException ex) { }
		responseObserver.onNext(result);
		responseObserver.onCompleted();
	}

	@Override
	public void subtract(ArithmeticOpArguments request,
			 StreamObserver<ArithmeticOpResult> responseObserver)
	{
		System.out.println("subtractRequest (" + request.getArg1() + ", " + request.getArg2() +")");
		int val = request.getArg1() - request.getArg2();
		ArithmeticOpResult result = ArithmeticOpResult.newBuilder().setRes(val).build();
		responseObserver.onNext(result);
		responseObserver.onCompleted();
	}

	@Override
	public void multiply(MultiplyArguments request,
						 StreamObserver<ArithmeticOpResult> responseObserver)
	{
		System.out.println("multiplyRequest (" + request.getArgsList() + ")");
		int val = request.getArgsList().stream().reduce(1, (a, b) -> a * b);
		ArithmeticOpResult result = ArithmeticOpResult.newBuilder().setRes(val).build();
		responseObserver.onNext(result);
		responseObserver.onCompleted();
	}

	@Override
	public void getWeightedAverage(WeightedAverageArguments request,
			   StreamObserver<WeightedAverageResult> responseObserver)
	{
		System.out.println("getWeightedAverageRequest (" + request.getGradesList() + ")");
		double val = request.getGradesList().stream().mapToDouble(a -> a.getValue() * a.getWeight()).sum();
		double weightSum = request.getGradesList().stream().mapToDouble(Grade::getWeight).sum();
		val = val / weightSum;
		WeightedAverageResult result = sr.grpc.gen.WeightedAverageResult.newBuilder().setRes(val).build();
		responseObserver.onNext(result);
		responseObserver.onCompleted();
	}

}
