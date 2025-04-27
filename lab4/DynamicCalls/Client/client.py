import grpc
from google.protobuf.descriptor_pool import DescriptorPool
from google.protobuf.message_factory import GetMessageClass
from grpc_reflection.v1alpha.proto_reflection_descriptor_database import ProtoReflectionDescriptorDatabase
import time


def main():
    with grpc.insecure_channel("127.0.0.5:50051") as channel:
        reflection_db = ProtoReflectionDescriptorDatabase(channel)
        desc_pool = DescriptorPool(reflection_db)

        services = reflection_db.get_services()
        print(f"Available services: {services}")

        # example_add(channel, desc_pool)
        # example_mul(channel, desc_pool)
        example_weighted_average(channel, desc_pool)
        # example_server_stream(channel, desc_pool)
        # example_client_stream(channel, desc_pool)


def example_add(channel: grpc.Channel, desc_pool: DescriptorPool):
    service_desc = desc_pool.FindServiceByName("calculator.Calculator")

    print(f"\n{service_desc.full_name}")
    print("-" * 20)
    print("Service methods:")
    for method in service_desc.methods:
        print(f"name: {method.name}, input: {method.input_type.name}, output: {method.output_type.name}")
    print("-" * 20)

    add_method = service_desc.methods[0]

    request_desc = add_method.input_type
    request_class = GetMessageClass(request_desc)

    request = request_class()
    request.arg1 = 2
    request.arg2 = 7

    print("-" * 20)
    print("Request fields:")
    for field in request.ListFields():
        print(field[0].name, field[1])
    print("-" * 20)

    output_desc = add_method.output_type
    output_class = GetMessageClass(output_desc)

    result = channel.unary_unary(
                    f'/{service_desc.full_name}/{add_method.name}',
                    request_serializer=request_class.SerializeToString,
                    response_deserializer=output_class.FromString
                )(request)

    print(f"Result: {result.res}")


def example_mul(channel: grpc.Channel, desc_pool: DescriptorPool):
    service_desc = desc_pool.FindServiceByName("calculator.Calculator")

    print(f"\n{service_desc.full_name}")
    print("-" * 20)
    print("Service methods:")
    for method in service_desc.methods:
        print(f"name: {method.name}, input: {method.input_type.name}, output: {method.output_type.name}")
    print("-" * 20)

    mul_method = service_desc.methods[2]

    request_desc = mul_method.input_type
    request_class = GetMessageClass(request_desc)

    request = request_class()
    request.args.extend([1, 2, 3, 4, 5])

    print("-" * 20)
    print("Request fields:")
    for field in request.ListFields():
        print(field[0].name, field[1])
    print("-" * 20)

    output_desc = mul_method.output_type
    output_class = GetMessageClass(output_desc)

    result = channel.unary_unary(
                    f'/{service_desc.full_name}/{mul_method.name}',
                    request_serializer=request_class.SerializeToString,
                    response_deserializer=output_class.FromString
                )(request)

    print(f"Result: {result.res}")


def example_weighted_average(channel: grpc.Channel, desc_pool: DescriptorPool):
    service_desc = desc_pool.FindServiceByName("calculator.Calculator")
    
    print(f"\n{service_desc.full_name}")
    print("-" * 20)
    print("Service methods:")
    for method in service_desc.methods:
        print(f"name: {method.name}, input: {method.input_type.name}, output: {method.output_type.name}")
    print("-" * 20)

    avg_method = service_desc.methods[3]

    request_desc = avg_method.input_type
    request_class = GetMessageClass(request_desc)

    print(request_desc.fields_by_name)

    grade_desc = request_desc.fields_by_name["grades"].message_type
    grade_class = GetMessageClass(grade_desc)

    request = request_class()
    
    grades = [
        (5.0, 0.5),
        (4.0, 0.3),
        (4.5, 0.2)
    ]

    for value, weight in grades:
        grade = grade_class()
        grade.value = value
        grade.weight = weight
        request.grades.append(grade)


    print("-" * 20)
    print("Request fields:")
    for field in request.ListFields():
        print(field[0].name, field[1])
    print("-" * 20)

    output_desc = avg_method.output_type
    output_class = GetMessageClass(output_desc)

    result = channel.unary_unary(
                    f'/{service_desc.full_name}/{avg_method.name}',
                    request_serializer=request_class.SerializeToString,
                    response_deserializer=output_class.FromString
                )(request)

    print(f"Result: {result.res}")


def example_server_stream(channel: grpc.Channel, desc_pool: DescriptorPool):
    service_desc = desc_pool.FindServiceByName("streaming.StreamTester")

    print(f"\n{service_desc.full_name}")
    print("-" * 20)
    print("Service methods:")
    for method in service_desc.methods:
        print(f"name: {method.name}, input: {method.input_type.name}, output: {method.output_type.name}")
    print("-" * 20)

    generate_prime_method = service_desc.methods[0]

    request_desc = generate_prime_method.input_type
    request_class = GetMessageClass(request_desc)

    request = request_class()
    request.max = 10

    print("-" * 20)
    print("Request fields:")
    for field in request.ListFields():
        print(field[0].name, field[1])
    print("-" * 20)

    output_desc = generate_prime_method.output_type
    output_class = GetMessageClass(output_desc)

    result = channel.unary_stream(
                    f'/{service_desc.full_name}/{generate_prime_method.name}',
                    request_serializer=request_class.SerializeToString,
                    response_deserializer=output_class.FromString
                )(request)
    
    print("Result:")
    for res in result:
        print(res.value)


def example_client_stream(channel: grpc.Channel, desc_pool: DescriptorPool):
    service_desc = desc_pool.FindServiceByName("streaming.StreamTester")

    print(f"\n{service_desc.full_name}")
    print("-" * 20)
    print("Service methods:")
    for method in service_desc.methods:
        print(f"name: {method.name}, input: {method.input_type.name}, output: {method.output_type.name}")
    print("-" * 20)

    generate_prime_method = service_desc.methods[1]

    request_desc = generate_prime_method.input_type
    request_class = GetMessageClass(request_desc)

    output_desc = generate_prime_method.output_type
    output_class = GetMessageClass(output_desc)

    def number_generator():
        for i in range(10):
            time.sleep(1)
            request = request_class()
            request.value = i
            yield request

    result = channel.stream_unary(
                    f'/{service_desc.full_name}/{generate_prime_method.name}',
                    request_serializer=request_class.SerializeToString,
                    response_deserializer=output_class.FromString
                )(number_generator())
    
    print(f"Result: {result.count}")


if __name__ == '__main__':
    main()
