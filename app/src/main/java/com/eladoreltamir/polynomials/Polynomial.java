package com.eladoreltamir.polynomials;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Polynomial {
    private final TreeMap<Integer, Double> pairs;

    public Polynomial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pairs = new TreeMap<>(Comparator.reverseOrder());
        } else {
            pairs = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));
        }
    }
    public Polynomial(String input) {
        this();
        input = input.replaceAll("\\s", "");
        String[] terms = input.split("(?=[-+])");
        for (String term : terms) {
            if (term.isEmpty()) throw new NumberFormatException("A term is empty.");
            double coefficient = 1.0;
            int exponent;
            if (term.charAt(0) == '-' || term.charAt(0) == '+') {
                if (term.charAt(0) == '-') {
                    coefficient = -1.0;
                }
                term = term.substring(1);
            }
            if (term.charAt(term.length() - 1) == '^') throw new NumberFormatException("No power given in a term.");
            String[] parts = term.split("\\^");
            int countX = countXs(term);
            if (countX > 1) {
                throw new NumberFormatException("Too many x in a term.");
            } else if (parts.length == 1 && countX == 1) {
                exponent = 1;
            } else if (parts.length == 1 && countX == 0) {
                exponent = 0;
            } else if (parts.length == 2) {
                exponent = Integer.parseInt(parts[1].trim());
            } else {
                throw new NumberFormatException("Too many powers given in a term.");
            }
            String coefficientPart = parts[0].replaceAll("x", "");
            if (!coefficientPart.isEmpty()) {
                double parsed = Double.parseDouble(coefficientPart);
                if (!Double.isFinite(parsed)) throw new NumberFormatException("Non numbers given.");
                coefficient *= parsed;
            }
            double currentCoefficient = getOrZero(exponent);
            put(exponent, currentCoefficient + coefficient);
        }
    }
    public Polynomial add(Polynomial polynomial) {
        Polynomial result = new Polynomial();
        Set<Integer> powers = new TreeSet<>();
        powers.addAll(pairs.keySet());
        powers.addAll(polynomial.pairs.keySet());
        for (Integer power: powers) {
            Double x = getOrZero(power);
            if (x == null) {
                x = 0.0;
            }
            Double y = polynomial.getOrZero(power);
            if (y == null) {
                y = 0.0;
            }
            double coefficient = x + y;
            result.put(power, coefficient);
        }
        return result;
    }
    public Polynomial subtract(Polynomial polynomial) {
        Polynomial result = new Polynomial();
        Set<Integer> powers = new TreeSet<>();
        powers.addAll(pairs.keySet());
        powers.addAll(polynomial.pairs.keySet());
        for (Integer power: powers) {
            Double x = getOrZero(power);
            if (x == null) {
                x = 0.0;
            }
            Double y = polynomial.getOrZero(power);
            if (y == null) {
                y = 0.0;
            }
            double coefficient = x - y;
            result.put(power, coefficient);
        }
        return result;
    }
    public Polynomial multiply(Polynomial polynomial) {
        Polynomial result = new Polynomial();
        for (TreeMap.Entry<Integer, Double> i: pairs.entrySet()) {
            for (TreeMap.Entry<Integer, Double> j: polynomial.pairs.entrySet()) {
                int power = i.getKey() + j.getKey();
                double coefficient = i.getValue() * j.getValue();
                Double x = result.getOrZero(power);
                if (x == null) {
                    x = 0.0;
                }
                result.put(power, x + coefficient);
            }
        }
        return result;
    }
    public Polynomial derivative() {
        Polynomial result = new Polynomial();
        for (TreeMap.Entry<Integer, Double> pair: pairs.entrySet()) {
            if (pair.getKey() == 0) continue;
            result.put(pair.getKey() - 1, pair.getKey() * pair.getValue());
        }
        return result;
    }
    public Polynomial integral(double constant) {
        Polynomial result = new Polynomial();
        for (TreeMap.Entry<Integer, Double> pair: pairs.entrySet()) {
            result.put(pair.getKey() + 1, pair.getValue() / (pair.getKey() + 1));
        }
        result.put(0, constant);
        return result;
    }
    public double calculate(double x) {
        double result = 0;
        for (TreeMap.Entry<Integer, Double> pair: pairs.entrySet()) {
            result += pair.getValue() * Math.pow(x, pair.getKey());
        }
        return result;
    }
    @NonNull
    public String toString() {
        if (pairs.isEmpty()) return String.valueOf(0.0);
        StringBuilder builder = new StringBuilder();
        String separator;
        boolean first = true;
        for (TreeMap.Entry<Integer, Double> pair: pairs.entrySet()) {
            if (pair.getValue() < 0 && !first) {
                separator = " - ";
            } else if (pair.getValue() < 0) {
                separator = "-";
            } else if (!first) {
                separator = " + ";
            } else {
                separator = "";
            }
            builder.append(separator);
            double coefficient = Math.abs(pair.getValue());
            if (pair.getKey() > 0) {
                if (Math.abs(pair.getValue()) != 1.0) {
                    builder.append(GUI.doubleToString(coefficient));
                }
                builder.append('x');
                if (pair.getKey() > 1) {
                    builder.append(GUI.intToSuperscript(pair.getKey()));
                }
            } else {
                builder.append(GUI.doubleToString(coefficient));
            }
            first = false;
        }
        return builder.toString();
    }
    public void put(int power, double coefficient) {
        if (power < 0) throw new NumberFormatException("Power must be positive.");
        if (coefficient == 0) {
            pairs.remove(power);
        } else {
            pairs.put(power, coefficient);
        }
    }
    public Set<TreeMap.Entry<Integer, Double>> entrySet() {
        return pairs.entrySet();
    }
    private Double getOrZero(Integer key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return pairs.getOrDefault(key, 0.0);
        if (pairs.containsKey(key)) return pairs.get(key);
        return 0.0;
    }
    private int countXs(String string) {
        int result = 0;
        for (int i = 0; i < string.length(); ++i) {
            if (string.charAt(i) == 'x') {
                ++result;
            }
        }
        return result;
    }
}
