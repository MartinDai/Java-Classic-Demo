package com.doodl6.demo.proxy;


import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 基于asm实现动态代理
 */
public class AsmProxyDemo {

    public static class Hello {

        public void sayHello() {
            System.out.println("hello world");
        }
    }

    private static class GeneratorClassLoader extends ClassLoader {

        public Class<?> defineClass(String className, byte[] classBytes) throws ClassFormatError {
            return defineClass(className, classBytes, 0, classBytes.length);
        }
    }

    public static class MyClassVisitor extends ClassVisitor {

        public MyClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM7, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("sayHello".equals(name)) {
                return new MyAdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor);
            }

            return methodVisitor;
        }
    }

    public static class MyAdviceAdapter extends AdviceAdapter {

        private final String methodName;

        protected MyAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            this.methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            addPrintString("before invoke method:" + methodName);
        }

        @Override
        protected void onMethodExit(int opcode) {
            addPrintString("after invoke method:" + methodName);
        }

        private void addPrintString(String str) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(str);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

    public static void main(String[] args) throws Throwable {
        // 创建ClassReader对象，根据类全路径名称读入代理类字节码
        ClassReader classReader = new ClassReader(Hello.class.getName());

        //创建ClassReader对象，用于对修改后的字节码数组回写
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);

        //创建自定义ClassVisitor对象
        ClassVisitor visitor = new MyClassVisitor(classWriter);
        classReader.accept(visitor, ClassReader.SKIP_FRAMES);

        //得到修改后的字节码数据
        byte[] byteArray = classWriter.toByteArray();

        Class<?> clazz = new GeneratorClassLoader().defineClass(Hello.class.getName(), byteArray);

        // 将修改后的字节码数据写入文件
        ClassUtil.saveClass(byteArray, clazz.getName());
        Object obj = clazz.newInstance();
        clazz.getMethod("sayHello").invoke(obj);

        System.out.println(clazz.getName());
    }

}