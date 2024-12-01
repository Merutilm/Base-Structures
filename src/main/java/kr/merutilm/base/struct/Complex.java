package kr.merutilm.base.struct;


public record Complex(double re, double im) implements Struct<Complex> {


    public static final Complex ZERO = new Complex(0, 0);

    public static final Complex ONE = new Complex(1, 0);


    @Override
    public Builder edit() {
        return new Builder(re,im);
    }

    public static final class Builder implements StructBuilder<Complex> { 
        private double re;
        private double im;

        public Builder(double re, double im){
            this.re = re;
            this.im = im;
        }

        public Builder add(Complex complex) {
            return add(complex.re(), complex.im());
        }
    
        public Builder add(double r, double i){
            re += r;
            im += i;
            return this;
        }
    
        public Builder addRe(double r){
            re += r;
            return this;
        }
    
        public Builder addIm(double i){
            im += i;
            return this;
        }
    
        public Builder subtract(Complex complex) {
            re -= complex.re;
            im -= complex.im;
            return this;
        }
        public Builder multiply(Complex complex) {
            double re = this.re * complex.re - this.im * complex.im;
            double im = this.re * complex.im + this.im * complex.re;
            this.re = re;
            this.im = im;
            return this;
        }
    
        public Builder multiply(double m){
            re *= m;
            im *= m;
            return this;
        }
    
        
        public Builder divide(Complex complex) {
            double divisor = complex.re * complex.re + complex.im * complex.im;
            double fre = this.re * complex.re + this.im * complex.im;
            double fim = this.im * complex.re - this.re * complex.im;
            this.re = fre / divisor;
            this.im = fim / divisor;
            return this;
        }
    
        public Builder divide(double d){
            re /= d;
            im /= d;
            return this;
        }
        
        public Builder exp() {
            double m = Math.pow(Math.E, this.re);
            double re = m * Math.cos(this.im);
            double im = m * Math.sin(this.im);
            this.re = re;
            this.im = im;
            return this;
        }

    
        public Builder ln() {
            double re = Math.log(this.re * this.re + this.im * this.im);
            double im = Math.atan2(this.im, this.re);
            this.re = re;
            this.im = im;
            return this;
        }

        @Override
        public Complex build() {
            return new Complex(re, im);
        }
    }

    public Complex add(Complex c){
        return new Complex(re + c.re, im + c.im);
    }

    public Complex subtract(Complex c){
        return new Complex(re - c.re, im - c.im);
    }

    public Complex multiply(double m){
        return new Complex(re * m, im * m);
    }
    
    public Complex multiply(Complex c){
        double re = this.re * c.re - this.im * c.im;
        double im = this.re * c.im + this.im * c.re;
        return new Complex(re, im);
    }

    public Complex divide(double d){
        return new Complex(re / d, im / d);
    }

    public double radius2() {
        return re * re + im * im;
    }

    public double radius(){
        return Math.sqrt(radius2());
    }

    @Override
    public String toString() {
        if (im > 0) {
            return (re == 0 ? "" : re + "+") + im + "i";
        }
        if (im < 0) {
            return (re == 0 ? "" : String.valueOf(re)) + im + "i";
        }
        return String.valueOf(re);
    }


}
