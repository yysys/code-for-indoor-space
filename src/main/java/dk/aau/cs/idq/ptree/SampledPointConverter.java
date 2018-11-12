package dk.aau.cs.idq.ptree;

import org.khelekore.prtree.MBRConverter;

import dk.aau.cs.idq.indoorentities.SampledPoint;


public class SampledPointConverter implements MBRConverter<SampledPoint>{

	@Override
	public int getDimensions() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public double getMin(int axis, SampledPoint t) {
		// TODO Auto-generated method stub
		if (0 == axis) {
			return t.getSampledX();
		} else if (1 == axis) {
			return t.getSampledY();
		} else
			return -1;
	}

	@Override
	public double getMax(int axis, SampledPoint t) {
		// TODO Auto-generated method stub
		if (0 == axis) {
			return t.getSampledX();
		} else if (1 == axis) {
			return t.getSampledY();
		} else
			return -1;
	}

}
